package sp.phone.http.bean;

import java.util.Map;

import gov.anzong.androidnga.common.base.JavaBean;

/**
 * Created by Justwen on 2017/11/23.
 */

public class TopicListBean implements JavaBean {

    /**
     * data : {"__CU":{"uid":000000,"group_bit":622816,"admincheck":"","rvrc":-10},"__GLOBAL":"./template/js/nga_global.xml","__F":{"topped_topic":"","sub_forums":""},"__ROWS":2,"__T":{"0":{"tid":11915941,"fid":275,"quote_from":0,"quote_to":"","topic_misc":"","author":"xxxxxx","authorid":000000,"subject":"客户端测试发帖","type":516,"postdate":1498529634,"lastpost":1499236460,"lastposter":"killmanasdfasdf","replies":13,"lastmodify":1500443085,"recommend":1,"titlefont":"","admin_ui":1,"tpcurl":"/read.php?tid=11915941&fav=c7cf9a59","parent":{"0":275,"2":"测试版面"}}},"__T__ROWS":1,"__T__ROWS_PAGE":35,"__R__ROWS_PAGE":20}
     * encode : gbk
     * time : 1511446433
     */

    private DataBean data;
    private String encode;
    private int time;

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

    public static class DataBean implements JavaBean {
        /**
         * __CU : {"uid":000000,"group_bit":622816,"admincheck":"","rvrc":-10}
         * __GLOBAL : ./template/js/nga_global.xml
         * __F : {"topped_topic":"","sub_forums":""}
         * __ROWS : 2
         * __T : {"0":{"tid":11915941,"fid":275,"quote_from":0,"quote_to":"","topic_misc":"","author":"xxxxxx","authorid":000000,"subject":"客户端测试发帖","type":516,"postdate":1498529634,"lastpost":1499236460,"lastposter":"killmanasdfasdf","replies":13,"lastmodify":1500443085,"recommend":1,"titlefont":"","admin_ui":1,"tpcurl":"/read.php?tid=11915941&fav=c7cf9a59","parent":{"0":275,"2":"测试版面"}}}
         * __T__ROWS : 1
         * __T__ROWS_PAGE : 35
         * __R__ROWS_PAGE : 20
         */

        private CUBean __CU;
        private String __GLOBAL;
        private FBean __F;
        private String __ROWS;
        private Map<String, TBean> __T;
        private int __T__ROWS;
        private int __T__ROWS_PAGE;
        private int __R__ROWS_PAGE;

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

        public String get__ROWS() {
            return __ROWS;
        }

        public void set__ROWS(String __ROWS) {
            this.__ROWS = __ROWS;
        }

        public Map<String, TBean> get__T() {
            return __T;
        }

        public void set__T(Map<String, TBean> __T) {
            this.__T = __T;
        }

        public int get__T__ROWS() {
            return __T__ROWS;
        }

        public void set__T__ROWS(int __T__ROWS) {
            this.__T__ROWS = __T__ROWS;
        }

        public int get__T__ROWS_PAGE() {
            return __T__ROWS_PAGE;
        }

        public void set__T__ROWS_PAGE(int __T__ROWS_PAGE) {
            this.__T__ROWS_PAGE = __T__ROWS_PAGE;
        }

        public int get__R__ROWS_PAGE() {
            return __R__ROWS_PAGE;
        }

        public void set__R__ROWS_PAGE(int __R__ROWS_PAGE) {
            this.__R__ROWS_PAGE = __R__ROWS_PAGE;
        }

        public static class CUBean implements JavaBean {
            /**
             * uid : 10350496
             * group_bit : 622816
             * admincheck :
             * rvrc : -10
             */

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
            /**
             * topped_topic :
             * sub_forums :
             */

            private String topped_topic;
            private String sub_forums;
            private int fid;
            public String name;

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public String getTopped_topic() {
                return topped_topic;
            }

            public void setTopped_topic(String topped_topic) {
                this.topped_topic = topped_topic;
            }

            public String getSub_forums() {
                return sub_forums;
            }

            public void setSub_forums(String sub_forums) {
                this.sub_forums = sub_forums;
            }
        }

        public static class TBean implements JavaBean {

            /**
             * tid : 11915941
             * fid : 275
             * quote_from : 0
             * quote_to :
             * topic_misc :
             * author : xxxxxx
             * authorid : 000000
             * subject : 客户端测试发帖
             * type : 516
             * postdate : 1498529634
             * lastpost : 1499236460
             * lastposter : killmanasdfasdf
             * replies : 13
             * lastmodify : 1500443085
             * recommend : 1
             * titlefont :
             * admin_ui : 1
             * tpcurl : /read.php?tid=11915941&fav=c7cf9a59
             * parent : {"0":275,"2":"测试版面"}
             * topic_misc_var:{"3":-1459709,"1":32},
             */

            private int tid;
            private int fid;
            private int quote_from;
            private String quote_to;
            private String topic_misc;
            private String author;
            private String authorid;
            private String subject;
            private int type;
            private int postdate;
            private int lastpost;
            private String lastposter;
            private int replies;
            private int lastmodify;
            private int recommend;
            private String titlefont;
            private int admin_ui;
            private String tpcurl;
            private PBean __P;
            /**
             * 0 : 275
             * 2 : 测试版面
             */
            public String parent;

            public Map<String, String> topic_misc_var;

            public int getTid() {
                return tid;
            }

            public void setTid(int tid) {
                this.tid = tid;
            }

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public int getQuote_from() {
                return quote_from;
            }

            public void setQuote_from(int quote_from) {
                this.quote_from = quote_from;
            }

            public String getQuote_to() {
                return quote_to;
            }

            public void setQuote_to(String quote_to) {
                this.quote_to = quote_to;
            }

            public String getTopic_misc() {
                return topic_misc;
            }

            public void setTopic_misc(String topic_misc) {
                this.topic_misc = topic_misc;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getAuthorid() {
                return authorid;
            }

            public void setAuthorid(String authorid) {
                this.authorid = authorid;
            }

            public String getSubject() {
                return subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getPostdate() {
                return postdate;
            }

            public void setPostdate(int postdate) {
                this.postdate = postdate;
            }

            public int getLastpost() {
                return lastpost;
            }

            public void setLastpost(int lastpost) {
                this.lastpost = lastpost;
            }

            public String getLastposter() {
                return lastposter;
            }

            public void setLastposter(String lastposter) {
                this.lastposter = lastposter;
            }

            public int getReplies() {
                return replies;
            }

            public void setReplies(int replies) {
                this.replies = replies;
            }

            public int getLastmodify() {
                return lastmodify;
            }

            public void setLastmodify(int lastmodify) {
                this.lastmodify = lastmodify;
            }

            public int getRecommend() {
                return recommend;
            }

            public void setRecommend(int recommend) {
                this.recommend = recommend;
            }

            public String getTitlefont() {
                return titlefont;
            }

            public void setTitlefont(String titlefont) {
                this.titlefont = titlefont;
            }

            public int getAdmin_ui() {
                return admin_ui;
            }

            public void setAdmin_ui(int admin_ui) {
                this.admin_ui = admin_ui;
            }

            public String getTpcurl() {
                return tpcurl;
            }

            public void setTpcurl(String tpcurl) {
                this.tpcurl = tpcurl;
            }

            public PBean get__P() {
                return __P;
            }

            public void set__P(PBean __P) {
                this.__P = __P;
            }

            @Override
            public String toString() {
                return "TBean{" +
                        "tid=" + tid +
                        ", fid=" + fid +
                        ", subject='" + subject + '\'' +
                        ", type=" + type +
                        ", recommend=" + recommend +
                        '}';
            }

            public static class PBean implements JavaBean {
                /**
                 * tid : 12937812
                 * pid : 253178256
                 * authorid : 39454545
                 * type : 512
                 * postdate : 1511787523
                 * subject :
                 * content : [quote][pid=253176649,12937812,2]Reply[/pid] [b]Post by [uid=42132919]宇宙超级无敌大帅逼[/uid] (2017-11-27 20:42):[/b]<br/><br/>大号术士痛苦75:毁灭75:恶魔73(恶魔圣物都只有两个)<br/>小号战士武器75:狂暴75:防御72<br/>肝还好，战士因为有地图炮真心清世界任务节约不少时间。配合组队插件，感觉刷小入侵和某些世界任务才是刷能量最快的方式，一天两波，打完下线。(其实有车队大米开车也还可以，比如噬魂这种本来就很快的本，不过现在找钥匙比以前慢了好多，而且一天一天毫无新意的循环，让人有点想吐，几个基友都已经AFK了，不知道我还能坚持多久，唉[s:ac:囧])[/quote]<br/><br/>老哥你真的算很肝的哦……两个号75
                 */

                private int tid;
                private int pid;
                private String authorid;
                private int type;
                private int postdate;
                private String subject;
                private String content;

                public int getTid() {
                    return tid;
                }

                public void setTid(int tid) {
                    this.tid = tid;
                }

                public int getPid() {
                    return pid;
                }

                public void setPid(int pid) {
                    this.pid = pid;
                }

                public String getAuthorid() {
                    return authorid;
                }

                public void setAuthorid(String authorid) {
                    this.authorid = authorid;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public int getPostdate() {
                    return postdate;
                }

                public void setPostdate(int postdate) {
                    this.postdate = postdate;
                }

                public String getSubject() {
                    return subject;
                }

                public void setSubject(String subject) {
                    this.subject = subject;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }
            }
        }
    }
}
