package tecrys.data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class macroupperlefteveryframe implements EveryFrameWeaponEffectPlugin {

    float index = 0f;
    IntervalUtil interval = new IntervalUtil(0.02f, 0.02f);

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }

        ShipAPI ship = weapon.getShip();

        WeaponAPI arm = null;

        WeaponAPI forearm = null;

        WeaponAPI hand = null;

        for (WeaponAPI w : ship.getAllWeapons()) {
            if (w.getSlot().getId().equals("WS0010")) {
                arm = w;
            }

            if (w.getSlot().getId().equals("WS0012")) {
                forearm = w;
            }

            if (w.getSlot().getId().equals("WS0014")) {
                hand = w;
            }
        }

        interval.advance(amount);

        if(interval.intervalElapsed()) {
            float offset = 1f;
            if (arm != null) {
                //float offset = 0f;

                arm.setCurrAngle(arm.getCurrAngle() + offset);
            }

            if (forearm != null) {
                forearm.getSlot().getLocation().set(VectorUtils.rotateAroundPivot(new Vector2f(arm.getSlot().getLocation().getX()+106f, arm.getSlot().getLocation().getY()), arm.getSlot().getLocation(), arm.getCurrAngle() - ship.getFacing()));

                //float offset = 0f;

                forearm.setCurrAngle(forearm.getCurrAngle()+2*offset);
            }

            if (hand != null) {
                hand.getSlot().getLocation().set(VectorUtils.rotateAroundPivot(new Vector2f(forearm.getSlot().getLocation().getX() + 120f, forearm.getSlot().getLocation().getY() - 4f), forearm.getSlot().getLocation(), forearm.getCurrAngle() - ship.getFacing()));

                //float offset = 0f;

                hand.setCurrAngle(hand.getCurrAngle()+3*offset);
            }
        }
    }
}