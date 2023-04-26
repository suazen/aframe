package me.suazen.aframe.system.core.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class Tables {


    public static final SysUserTableDef SYS_USER = new SysUserTableDef("sys_user");

    public static class SysUserTableDef extends TableDef {

        public QueryColumn USER_ID = new QueryColumn(this, "user_id");
        public QueryColumn USERNAME = new QueryColumn(this, "username");
        public QueryColumn LOGIN_NAME = new QueryColumn(this, "login_name");
        public QueryColumn PASSWORD = new QueryColumn(this, "password");
        public QueryColumn LOGIN_IP = new QueryColumn(this, "login_ip");
        public QueryColumn LOGIN_DATE = new QueryColumn(this, "login_date");
        public QueryColumn CREATOR = new QueryColumn(this, "creator");
        public QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
        public QueryColumn UPDATER = new QueryColumn(this, "updater");
        public QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

        public QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{USER_ID, USERNAME, LOGIN_NAME, PASSWORD, LOGIN_IP, LOGIN_DATE, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME};
        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{USER_ID, USERNAME, LOGIN_NAME, PASSWORD, LOGIN_IP, LOGIN_DATE, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME};


        public SysUserTableDef(String tableName) {
            super(tableName);
        }
    }
}
