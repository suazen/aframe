package me.suazen.aframe.module.echo.openai.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.core.manager.AsyncManager;
import me.suazen.aframe.module.echo.common.constants.RedisKey;
import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.common.dto.ChatRequest;
import me.suazen.aframe.module.echo.common.entity.ChatHis;
import me.suazen.aframe.module.echo.common.entity.Member;
import me.suazen.aframe.module.echo.common.exception.UsageLimitException;
import me.suazen.aframe.module.echo.common.util.AzureOpenaiUtil;
import me.suazen.aframe.module.echo.common.util.StpWxUtil;
import me.suazen.aframe.module.echo.config.tasker.SaveChatHistoryTasker;
import me.suazen.aframe.module.echo.member.service.MemberService;
import me.suazen.aframe.module.echo.openai.dto.ChatDTO;
import me.suazen.aframe.module.echo.openai.handler.OpenaiStreamEventListener;
import me.suazen.aframe.module.echo.openai.service.OpenaiService;
import me.suazen.aframe.web.sse.SseServer;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenaiServiceImpl implements OpenaiService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private MemberService memberService;

    @Override
    public SseEmitter sendMessage(ChatDTO dto) {
        String userId = (String) StpWxUtil.stpLogic.getLoginId();
        RAtomicLong times = memberService.getUsageFromRedis(userId);
        //redis缓存中次数小于0并且执行更新操作后仍小于0
        if (times.get() <= 0){
            int remains = memberService.initMemberUsage(userId);
            if (remains  <= 0) {
                Member member = new Member().userId().eq(userId).one();
                throw new UsageLimitException(member.getMemberType(),remains);
            }
        }
        SseEmitter sseEmitter = SseServer.builder(Duration.ofMinutes(5).toMillis()).build();
        //从redis获取聊天记录
        RList<ChatMessage> messages = getChatMessages(dto.getUuid());
        //如果有content则添加，没有表示重新回答
        if (StrUtil.isNotBlank(dto.getContent())) {
            messages.add(ChatMessage.userMsg(dto.getContent().trim()));
            AsyncManager.me().execute(new SaveChatHistoryTasker(userId,dto.getUuid(),"user",dto.getContent().trim(),messages.size()-1));
        }
        //调用openai接口
        AzureOpenaiUtil.callStream(new ChatRequest().setMessages(messages.readAll()), new OpenaiStreamEventListener(sseEmitter) {
            @Override
            public void onComplete() {
                times.decrementAndGet();
                //保存接口返回的完整内容
                messages.add(ChatMessage.botMsg(getContent()));
                AsyncManager.me().execute(new SaveChatHistoryTasker(userId,dto.getUuid(),"assistant",getContent(),messages.size()-1));
                messages.expire(Duration.ofHours(2));
            }
        });
        return sseEmitter;
    }

    @Override
    public SseEmitter reGenerate(String uuid, int index) {
        RList<ChatMessage> messages = getChatMessages(uuid);
        if (index > messages.size()){
            throw new BusinessException("系统异常，无法重新生成回答");
        }
        messages.removeAll(messages.subList(index,messages.size()));
        return sendMessage(new ChatDTO(uuid,null));
    }

    private RList<ChatMessage> getChatMessages(String uuid){
        RList<ChatMessage> redisList = redissonClient.getList(RedisKey.Folder.openai_chat_his.key(uuid));
        if (redisList.isEmpty()){
            List<ChatHis> chatHisList = new ChatHis()
                    .conversationId().eq(uuid)
                    .chatIndex().orderByAsc()
                    .list();
            redisList.addAll(chatHisList.stream().map(chatHis -> new ChatMessage(chatHis.getRole(),chatHis.getContent())).collect(Collectors.toList()));
        }
        return redisList;
    }

}
