package tecrys.data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class macroupperlefteveryframe implements EveryFrameWeaponEffectPlugin {

    float index = 0f;
    IntervalUtil interval = new IntervalUtil(0.01f, 0.01f);

    private static org.apache.log4j.Logger log = Global.getLogger(macroupperlefteveryframe.class);

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
            Vector2f targloc = ship.getMouseTarget();
            float offset = 1f;
            if (arm != null) {
                //float offset = 0f;

                Vector2f slotabsloc = VectorUtils.rotateAroundPivot(new Vector2f(arm.getSlot().getLocation().getX()+ship.getLocation().getX(), arm.getSlot().getLocation().getY()+ship.getLocation().getY()),ship.getLocation(), ship.getFacing());
                float dist = MathUtils.getDistance(slotabsloc,targloc)*0.4f;

                float targangle = (float) (Math.asin((MathUtils.clamp(dist,0f,106f)/106f)))*57.2958f;

                log.info(dist);
                log.info(targangle);

                float dangle = (3600f+MathUtils.clamp(VectorUtils.getAngle(slotabsloc,targloc)+90f - targangle,-10f+ship.getFacing(), 110f+ship.getFacing()))%360f;
                float currangle =  (3600f+arm.getCurrAngle())%360f;

                if(dangle > currangle+3f){
                    arm.setCurrAngle(currangle+3f);
                }else if(dangle < currangle-3f){
                    arm.setCurrAngle(currangle-3f);
                }

            }

            if (forearm != null) {
                forearm.getSlot().getLocation().set(VectorUtils.rotateAroundPivot(new Vector2f(arm.getSlot().getLocation().getX()+106f, arm.getSlot().getLocation().getY()), arm.getSlot().getLocation(), arm.getCurrAngle() - ship.getFacing()));

                //float offset = 0f;

                Vector2f slotabsloc = VectorUtils.rotateAroundPivot(new Vector2f(arm.getSlot().getLocation().getX()+ship.getLocation().getX(), arm.getSlot().getLocation().getY()+ship.getLocation().getY()),ship.getLocation(), ship.getFacing());
                float dist = MathUtils.getDistance(slotabsloc,targloc)*0.6f;

                float targangle = (float) (Math.asin((MathUtils.clamp(dist,0f,119f)/119f)))*57.2958f;

                float dangle = (3600f+VectorUtils.getAngle(slotabsloc,targloc)+90f - 180f + targangle)%360f;
                float currangle =  (3600f+forearm.getCurrAngle())%360f;

                if(dangle > currangle+3f){
                    forearm.setCurrAngle(currangle+3f);
                }else if(dangle < currangle-3f){
                    forearm.setCurrAngle(currangle-3f);
                }
            }

            if (hand != null) {
                hand.getSlot().getLocation().set(VectorUtils.rotateAroundPivot(new Vector2f(forearm.getSlot().getLocation().getX() + 120f, forearm.getSlot().getLocation().getY() - 4f), forearm.getSlot().getLocation(), forearm.getCurrAngle() - ship.getFacing()));

                //float offset = 0f;

                hand.setCurrAngle(forearm.getCurrAngle());
            }
        }
    }
}