package iqq.app.ui.component;

import com.alee.extended.painter.ColorPainter;
import com.alee.laf.button.WebButton;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import iqq.api.bean.IMStatus;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.I18nService;
import iqq.app.core.service.ResourceService;
import iqq.app.core.service.impl.I18nServiceImpl;
import iqq.app.core.service.impl.ResourceServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/*
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-04-19
 * License  : Apache License 2.0
 */
public class StatusButton extends WebButton {
    ResourceService resourceService = IMContext.getBean(ResourceServiceImpl.class);
    I18nService i18nService = IMContext.getBean(I18nServiceImpl.class);
    WebPopupMenu popupMenu = new WebPopupMenu ();
    Map<IMStatus, WebMenuItem> statusMap = new HashMap<IMStatus, WebMenuItem>();
    IMStatus currentStatus = IMStatus.ONLINE;
    int iconSize = 14;
    public StatusButton(IMStatus currentStatus) {
        this.setPainter(new ColorPainter(new Color(0, 0, 0, 0)));
        initList(currentStatus);
    }

    public StatusButton(IMStatus currentStatus, int iconSize) {
        this.iconSize = iconSize;
        this.setPainter(new ColorPainter(new Color(0, 0, 0, 0)));
        initList(currentStatus);
    }

    private void initList(IMStatus currentStatus) {
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(IMStatus status : statusMap.keySet()) {
                    if(statusMap.get(status) == e.getSource()) {
                        System.out.println(status);
                        setStatus(status);
                    }
                }
            }
        };
        String prefix = "icons/status/";
        if(currentStatus != null) {
            this.setIcon(resourceService.getIcon(prefix + currentStatus.toString().toLowerCase() + ".png", iconSize, iconSize));
            this.currentStatus = currentStatus;
        } else {
            this.setIcon(resourceService.getIcon(prefix + "online.png", iconSize, iconSize));
            this.currentStatus = IMStatus.ONLINE;
        }
        for(IMStatus status : IMStatus.values()) {
            String text = i18nService.getMessage("status." + status.toString().toLowerCase());
            Icon icon = resourceService.getIcon(prefix + status.toString().toLowerCase() + ".png", iconSize, iconSize);
            statusMap.put(status, new WebMenuItem(text, icon));
            popupMenu.add(statusMap.get(status));
            statusMap.get(status).addActionListener(listener);
        }

        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (popupMenu.isShowing()) {
                    popupMenu.hide();
                } else {
                    popupMenu.showBelow(StatusButton.this);
                }
            }
        });
    }

    public IMStatus getStatus() {
        return currentStatus;
    }

    public void setStatus(IMStatus status) {
        currentStatus = status;
        String prefix = "icons/status/";
        this.setIcon(resourceService.getIcon(prefix + currentStatus.toString().toLowerCase() + ".png", iconSize, iconSize));
    }

}
