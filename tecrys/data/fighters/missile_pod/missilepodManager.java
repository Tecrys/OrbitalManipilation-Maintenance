package tecrys.data.fighters.missile_pod;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

//manager listener for the xyphoses, stores data for them
//behaviour for these bad boys - hang around the sides of your ship
public class missilepodManager implements AdvanceableListener {

    public final ShipAPI mothership;

    public final ArrayList<ShipAPI> drones = new ArrayList<>(); //list of all drones

    public missilepodManager(ShipAPI mothership) {
        this.mothership = mothership;

        for (FighterWingAPI wing : mothership.getAllWings()) {
            if (wing.getSpec().getId().equals("omm_missilepod_wing")) {
                numDrones += 2;
            }
        }
    }

    float numDrones = 0; //the actual number of drones
    float angleFromBasepos = 90f;
    float distBetweenDrones = 60f;
    float anglebetweenDrones = 170f;
    float facingOffset = 30f;
    float shieldWidthOffsetMult = 0.8f;

    private final IntervalUtil deadDroneInterval = new IntervalUtil(0.2f, 0.2f);

    //todo relative offset so this is closer to the middle of the ship w/ fewer wings?
    //generally need to improve the point selection for this

    public Vector2f getDesiredPosition(ShipAPI drone) {

        Vector2f basePos = MathUtils.getPointOnCircumference(mothership.getShieldCenterEvenIfNoShield(), mothership.getShieldRadiusEvenIfNoShield(), mothership.getFacing());
        int index = drones.indexOf(drone);

        if (index % 2 == 0) { //if we're an even number
            Vector2f pos1 = MathUtils.getPointOnCircumference(basePos, mothership.getShieldRadiusEvenIfNoShield() * shieldWidthOffsetMult, mothership.getFacing() + angleFromBasepos); //star point on this line
            if (index == 0) return pos1;
            return MathUtils.getPointOnCircumference(pos1, (distBetweenDrones * index), mothership.getFacing() + anglebetweenDrones);
        } else { //if we're an odd number
            Vector2f pos1 = MathUtils.getPointOnCircumference(basePos, mothership.getShieldRadiusEvenIfNoShield() * shieldWidthOffsetMult, mothership.getFacing() - angleFromBasepos); //where index 1 on this side should go
            if (index == 1) return pos1;
            return MathUtils.getPointOnCircumference(pos1, (distBetweenDrones * (index - 1)), mothership.getFacing() - anglebetweenDrones);
        }
    }

    public float getDesiredFacing(ShipAPI drone) {

        int index = drones.indexOf(drone);

        if (index % 2 == 0) { //if we're an even number
            return mothership.getFacing() + facingOffset;
        } else { //if we're an odd number
            return mothership.getFacing() - facingOffset;
        }
    }

    //remove drones if they're dead or otherwise gone
    @Override
    public void advance(float amount) {
        deadDroneInterval.advance(amount);
        if (deadDroneInterval.intervalElapsed()) {
            for (ShipAPI drone : new ArrayList<>(drones)) {
                //Global.getCombatEngine().addFloatingText(drone.getLocation(), drone.getWing().getWingMembers().indexOf(drone) + ", " + drone.getWing().getSourceShip().getAllWings().indexOf(drone.getWing()), 20f, Color.BLUE, drone, 1f, 1f);
                if (!drone.isAlive() || drone.isHulk() || !Global.getCombatEngine().isEntityInPlay(drone)) {
                    drones.remove(drone);
                }
            }
        }
    }
}
