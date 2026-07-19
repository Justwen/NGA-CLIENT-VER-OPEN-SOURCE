package sp.phone.http.bean;

import gov.anzong.androidnga.common.base.JavaBean;

/**
 * Created by Justwen on 2017/6/10.
 */

public class TopicPostBean implements JavaBean {

    private DataBean data;
    private String encode;
    private int time;
    private Object debug;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Object getDebug() {
        return debug;
    }

    public void setDebug(Object debug) {
        this.debug = debug;
    }

    public static class DataBean implements JavaBean {

        private String action;
        private int fid;
        private String auth;
        private int if_moderator;
        private String tid;
        private CUBean __CU;
        private String __GLOBAL;
        private FBean __F;
        private String attach_url;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public int getFid() {
            return fid;
        }

        public void setFid(int fid) {
            this.fid = fid;
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }

        public int getIf_moderator() {
            return if_moderator;
        }

        public void setIf_moderator(int if_moderator) {
            this.if_moderator = if_moderator;
        }

        public String getTid() {
            return tid;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        public CUBean get__CU() {
            return __CU;
        }

        public void set__CU(CUBean __CU) {
            this.__CU = __CU;
        }

        public String get__GLOBAL() {
            return __GLOBAL;
        }

        public void set__GLOBAL(String __GLOBAL) {
            this.__GLOBAL = __GLOBAL;
        }

        public FBean get__F() {
            return __F;
        }

        public void set__F(FBean __F) {
            this.__F = __F;
        }

        public String getAttach_url() {
            return attach_url;
        }

        public void setAttach_url(String attach_url) {
            this.attach_url = attach_url;
        }

        public static class CUBean implements JavaBean {

            private int uid;
            private int group_bit;
            private String admincheck;
            private int rvrc;

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public int getGroup_bit() {
                return group_bit;
            }

            public void setGroup_bit(int group_bit) {
                this.group_bit = group_bit;
            }

            public String getAdmincheck() {
                return admincheck;
            }

            public void setAdmincheck(String admincheck) {
                this.admincheck = admincheck;
            }

            public int getRvrc() {
                return rvrc;
            }

            public void setRvrc(int rvrc) {
                this.rvrc = rvrc;
            }
        }

        public static class FBean implements JavaBean {

            private int bit_data;
            private int fid;
            private String name;

            public int getBit_data() {
                return bit_data;
            }

            public void setBit_data(int bit_data) {
                this.bit_data = bit_data;
            }

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
