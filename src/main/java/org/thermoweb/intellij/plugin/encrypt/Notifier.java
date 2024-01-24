package org.thermoweb.intellij.plugin.encrypt;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notifier {

    private Notifier() {}

    public static void notifyError(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("jasypt encryption error")
                .createNotification("Jasypt encrypt text plugin", content, NotificationType.ERROR)
                .notify(project);
    }
}
