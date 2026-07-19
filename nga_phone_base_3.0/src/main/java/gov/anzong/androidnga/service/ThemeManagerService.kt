package gov.anzong.androidnga.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.justwen.androidnga.base.service.api.IThemeManagerService
import sp.phone.theme.ThemeManager

@Route(path = IThemeManagerService.ROUTER_PATH)
class ThemeManagerService : IThemeManagerService {
    override fun isNightMode(): Boolean {
        return ThemeManager.getInstance().isNightMode()
    }

    override fun getThemeIndex(): Int {
        return ThemeManager.getInstance().getThemeIndex()
    }

    override fun getTheme(): Int {
        return ThemeManager.getInstance().getTheme(true)
    }

    override fun init(p0: Context?) {
    }
}