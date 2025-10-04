package sp.phone.common;

import androidx.annotation.Nullable;

import gov.anzong.androidnga.common.base.JavaBean;

public class FilterKeyword implements JavaBean {
  private String keyword;
  private boolean enabled;

  public FilterKeyword() {}

  public FilterKeyword(String keyword) {
    this.keyword = keyword;
    this.enabled = true;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    return obj instanceof FilterKeyword && ((FilterKeyword) obj).getKeyword().equals(keyword);
  }

  public String getKeyword() { return keyword; }
  public void setKeyword(String keyword) { this.keyword = keyword; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
}