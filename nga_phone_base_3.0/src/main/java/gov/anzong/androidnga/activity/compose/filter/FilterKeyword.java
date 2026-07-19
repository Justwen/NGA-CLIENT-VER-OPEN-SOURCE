package gov.anzong.androidnga.activity.compose.filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.common.base.JavaBean;

public class FilterKeyword implements JavaBean {
    private String keyword;

    @JSONField(serialize = false)
    private Pattern mPattern;

    private boolean enabled;

    public FilterKeyword() {
    }

    public FilterKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof FilterKeyword && ((FilterKeyword) obj).getKeyword().equals(keyword);
    }

    public boolean match(String content) {
        if (mPattern == null) {
            mPattern = Pattern.compile(keyword);
        }
        Matcher matcher = mPattern.matcher(content);
        return matcher.find();
    }

    @NonNull
    @Override
    public String toString() {
        return keyword;
    }

  public String getKeyword() { return keyword; }
  public void setKeyword(String keyword) { this.keyword = keyword; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
}