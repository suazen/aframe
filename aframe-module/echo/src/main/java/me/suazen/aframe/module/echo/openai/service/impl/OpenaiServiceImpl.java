package me.suazen.aframe.module.echo.openai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.constants.GlobalConstant;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.core.manager.AsyncManager;
import me.suazen.aframe.core.util.RandomUtil;
import me.suazen.aframe.web.util.ServletUtil;
import me.suazen.aframe.module.echo.common.constants.Constant;
import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.common.dto.ChatRequest;
import me.suazen.aframe.module.echo.common.entity.ChatHint;
import me.suazen.aframe.module.echo.common.entity.ChatHis;
import me.suazen.aframe.module.echo.common.tasker.SaveChatHistoryTasker;
import me.suazen.aframe.module.echo.common.util.AzureOpenaiUtil;
import me.suazen.aframe.module.echo.openai.dto.ChatDTO;
import me.suazen.aframe.module.echo.openai.handler.OpenaiStreamHandler;
import me.suazen.aframe.module.echo.openai.service.OpenaiService;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenaiServiceImpl implements OpenaiService {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void sendMessage(ChatDTO dto) {
        //获取HttpServletResponse，设置请求头并传入流解析器
        HttpServletResponse response = ServletUtil.getResponse();
        response.setContentType("text/event-stream;charset=UTF-8");
        OpenaiStreamHandler streamHandler = new OpenaiStreamHandler(response);
        //从redis获取聊天记录
        RList<ChatMessage> messages = getChatMessages(dto.getUuid());
        //如果有content则添加，没有表示重新回答
        if (StrUtil.isNotBlank(dto.getContent())) {
            messages.add(ChatMessage.userMsg(dto.getContent().trim()));
            AsyncManager.me().execute(new SaveChatHistoryTasker((String) StpUtil.getLoginId(),dto.getUuid(),"user",dto.getContent().trim(),messages.size()-1));
        }
        //调用openai接口
        AzureOpenaiUtil.callStream(new ChatRequest().setMessages(messages.readAll()),streamHandler);
        //保存接口返回的完整内容
        messages.add(ChatMessage.botMsg(streamHandler.getContent()));
        AsyncManager.me().execute(new SaveChatHistoryTasker((String) StpUtil.getLoginId(),dto.getUuid(),"assistant",streamHandler.getContent(),messages.size()-1));
    }

    @Override
    public void reGenerate(String uuid, int index) {
        RList<ChatMessage> messages = getChatMessages(uuid);
        if (index > messages.size()){
            throw new BusinessException("系统异常，无法重新生成回答");
        }
        messages.removeAll(messages.subList(index,messages.size()));
        sendMessage(new ChatDTO(uuid,null));
    }

    @Override
    public List<String> queryHint(String query) {
        RList<String> hintList = redissonClient.getList(Constant.REDIS_KEY_CHAT_HINT);
        if (!hintList.isExists()){
            List<ChatHint> hints = new ChatHint().select(ChatHint.CONTENT).state().eq(GlobalConstant.YES).list();
            hintList.addAll(hints.stream().map(ChatHint::getContent).collect(Collectors.toList()));
        }
        if (hintList.isEmpty()){
            return Collections.emptyList();
        }
        if ("all".equals(query)){
            return hintList.readAll();
        }
        if (StrUtil.isEmpty(query)){
            int length = Math.min(hintList.size(),6);
            return hintList.get(RandomUtil.randomInts(0,hintList.size()-1,length));
        }
        return hintList.readAll()
                .stream()
                .filter(hint-> {
                    String[] splits = query.split(" ");
                    for (String split : splits) {
                        if (hint.toLowerCase().contains(split.toLowerCase())){
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    private RList<ChatMessage> getChatMessages(String uuid){
        RList<ChatMessage> redisList = redissonClient.getList(Constant.REDIS_KEY_CHAT_HIS +uuid);
        redisList.expire(Duration.ofDays(1));
        if (redisList.isEmpty()){
            List<ChatHis> chatHisList = new ChatHis()
                    .conversationId().eq(uuid)
                    .chatIndex().orderByAsc()
                    .list();
            chatHisList.forEach(chatHis -> redisList.add(new ChatMessage(chatHis.getRole(),chatHis.getContent())));
        }
        return redisList;
    }
}
