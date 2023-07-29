package tecrys.data.utils;

import com.fs.starfarer.api.combat.ShipAPI;
//original script by ruddygreat
public class utils {

    public static <T> T getFirstListenerOfClass(ShipAPI ship, Class listenerClass) {

        if (!ship.hasListenerOfClass(listenerClass)) {
            return null;
        }

        Object listener = ship.getListeners(listenerClass).get(0);

        return (T) listener;
    }

}
