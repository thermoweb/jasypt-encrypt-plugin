package org.thermoweb.intellij.plugin.encrypt;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notifier {

    private static final String NOTIFICATION_GROUP = "jasypt encryption notifications";
    private static final String JASYPT_ENCRYPT_NOTIFICATION_TITLE = "Jasypt encrypt text plugin";

    private Notifier() {}

    public static void notifyError(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(NOTIFICATION_GROUP)
                .createNotification(JASYPT_ENCRYPT_NOTIFICATION_TITLE, content, NotificationType.ERROR)
                .notify(project);
    }
}
