package sp.phone.http.bean;

import java.util.List;

import gov.anzong.androidnga.common.base.JavaBean;

/**
 * Created by Justwen on 2019/6/16.
 */
public class BoardBean implements JavaBean {

    /**
     * name : 综合讨论
     * content : [{"fid":7,"name":"艾泽拉斯议事厅","info":"魔兽主讨论区","nameS":"议事厅"},{"fid":310,"name":"精英议会","info":"新版本与高阶讨论","infoS":"高阶讨论"},{"fid":323,"name":"国服以外讨论"},{"fid":10,"name":"银色黎明","info":"投诉/网站BUG/建议"},{"fid":230,"name":"艾泽拉斯风纪委员会","info":"曝光违背公认准则的行为","nameS":"风纪委员会"},{"fid":335,"name":"论坛开发"}]
     * id : bliz
     */

    public String name;
    public String id;
    public List<ContentBean> content;

    public static class ContentBean implements JavaBean {
        /**
         * fid : 7
         * name : 艾泽拉斯议事厅
         * info : 魔兽主讨论区
         * nameS : 议事厅
         * infoS : 高阶讨论
         */

        public int fid;
        public String name;
        public String info;
        public String nameS;
        public String infoS;
        public int stid;
        public String head;
    }
}
