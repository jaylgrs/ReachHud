public final class Notification {

    private static boolean visible;
    private static boolean enabled;
    private static long hideAt;

    private static final long DISPLAY_TIME = 2000L;

    private Notification() {
    }

    public static void show(boolean enabled) {
        Notification.enabled = enabled;
        Notification.visible = true;
        Notification.hideAt =
                System.currentTimeMillis() + DISPLAY_TIME;
    }

    public static boolean isVisible() {
        if (!visible) {
            return false;
        }

        if (System.currentTimeMillis() >= hideAt) {
            visible = false;
            return false;
        }

        return true;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
