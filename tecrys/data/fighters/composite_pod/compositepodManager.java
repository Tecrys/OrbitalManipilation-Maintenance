package tecrys.data.fighters.composite_pod;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class compositepodManager implements AdvanceableListener {

    public final ShipAPI mothership;

    public final ArrayList<ShipAPI> dronescomposite = new ArrayList<>(); //list of all drones
    public final ArrayList<FighterWingAPI> relevantWings = new ArrayList<>(); //the list of sarissa wings

    private final IntervalUtil deadDroneInterval = new IntervalUtil(0.2f, 0.2f);

    public compositepodManager(ShipAPI mothership) {
        this.mothership = mothership;

        for (FighterWingAPI wing : mothership.getAllWings()) {
            if (wing.getSpec().getId().equals("omm_compositepod_wing")) {
                relevantWings.add(wing);
            }
        }
    }

    float angleFromBasepos = 270f;
    float distBetweenClusters = 90f;
    float distFromClusterCenterToDrone = -110f;

    //each wing groups up into a triangle
    //odd no. of wings has one in center + 2 on each side, even no. of wings has non in center & all on sides
    //if in engage mode, stick closer to shield & rotate to face shield facing

    //todo need to give the sprite for these guys a second look, too bright vs the xyphos equivalent
    //todo need to do something for if the index per wing goes over 3 lol

    public Vector2f getDesiredPosition(ShipAPI drone) {

        int wingIndex = relevantWings.indexOf(drone.getWing());
        int indexInWing = drone.getWing().getWingMembers().indexOf(drone);
        int oddOrEvenNoOfWings = relevantWings.size() % 2;
        float anglebetweenDrones = 360f / drone.getWing().getSpec().getNumFighters();

        //todo better idea for defensive formation?
        if (mothership.isPullBackFighters()) {//defensive formation
            Vector2f basePos = MathUtils.getPointOnCircumference(mothership.getShieldCenterEvenIfNoShield(), mothership.getShieldRadiusEvenIfNoShield(), mothership.getFacing());
            if (oddOrEvenNoOfWings == 0) { //even number of wings, have none in the center
                Vector2f clusterCenter;//the center of this wing's cluster
                if (wingIndex % 2 == 0) { //wing index is even, go to the left
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, distBetweenClusters + (distBetweenClusters * wingIndex), mothership.getFacing() + angleFromBasepos);
                } else { //else, go to the right
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, distBetweenClusters + (distBetweenClusters * (wingIndex - 1)), mothership.getFacing() - angleFromBasepos);
                }
                return MathUtils.getPointOnCircumference(clusterCenter, distFromClusterCenterToDrone, mothership.getFacing() + (indexInWing * anglebetweenDrones));
            } else { //odd number of wings
                Vector2f clusterCenter;//the center of this wing's cluster
                if (wingIndex == 0) {
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, 0, mothership.getFacing() + angleFromBasepos);
                } else if (wingIndex % 2 == 0) { //wing index is even, go to the left
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, (distBetweenClusters * (wingIndex - 1)) * 2f, mothership.getFacing() + angleFromBasepos);
                } else { //else, go to the right
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, (distBetweenClusters * wingIndex) * 2f, mothership.getFacing() - angleFromBasepos);
                }
                return MathUtils.getPointOnCircumference(clusterCenter, distFromClusterCenterToDrone, mothership.getFacing() + (indexInWing * anglebetweenDrones));
            }
        } else { //offensive formation
            float desiredFacing = VectorUtils.getAngle(mothership.getShieldCenterEvenIfNoShield(), mothership.getMouseTarget());
            Vector2f basePos = MathUtils.getPointOnCircumference(mothership.getShieldCenterEvenIfNoShield(), mothership.getShieldRadiusEvenIfNoShield(), desiredFacing);
            if (oddOrEvenNoOfWings == 0) { //even number of wings, have none in the center
                Vector2f clusterCenter;//the center of this wing's cluster
                if (wingIndex % 2 == 0) { //wing index is even, go to the left
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, distBetweenClusters + (distBetweenClusters * wingIndex), desiredFacing + angleFromBasepos);
                } else { //else, go to the right
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, distBetweenClusters + (distBetweenClusters * (wingIndex - 1)), desiredFacing - angleFromBasepos);
                }
                return MathUtils.getPointOnCircumference(clusterCenter, distFromClusterCenterToDrone, desiredFacing + (indexInWing * anglebetweenDrones));
            } else { //odd number of wings
                Vector2f clusterCenter;//the center of this wing's cluster
                if (wingIndex == 0) {
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, 0, desiredFacing + angleFromBasepos);
                } else if (wingIndex % 2 == 0) { //wing index is even, go to the left
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, (distBetweenClusters * (wingIndex - 1)) * 2f, desiredFacing + angleFromBasepos);
                } else { //else, go to the right
                    clusterCenter = MathUtils.getPointOnCircumference(basePos, (distBetweenClusters * wingIndex) * 2f, desiredFacing - angleFromBasepos);
                }
                return MathUtils.getPointOnCircumference(clusterCenter, distFromClusterCenterToDrone, desiredFacing + (indexInWing * anglebetweenDrones));
            }
        }
    }

    public float getDesiredFacing(ShipAPI drone) {

        if (mothership.isPullBackFighters()) {//defensive formation
            return mothership.getFacing();
        } else { //offsenive formation
            return VectorUtils.getAngle(mothership.getShieldCenterEvenIfNoShield(), mothership.getMouseTarget());
        }
    }

    //remove drones if they're dead or otherwise gone
    @Override
    public void advance(float amount) {
        deadDroneInterval.advance(amount);
        if (deadDroneInterval.intervalElapsed()) {
            for (ShipAPI drone : new ArrayList<>(dronescomposite)) {
                //Global.getCombatEngine().addFloatingText(drone.getLocation(), drone.getWing().getWingMembers().indexOf(drone) + ", " + drone.getWing().getSourceShip().getAllWings().indexOf(drone.getWing()), 20f, Color.BLUE, drone, 1f, 1f);
                if (!drone.isAlive() || drone.isHulk() || !Global.getCombatEngine().isEntityInPlay(drone)) {
                    dronescomposite.remove(drone);
                }
            }
        }
    }
}
