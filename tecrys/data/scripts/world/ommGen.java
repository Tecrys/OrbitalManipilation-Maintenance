package tecrys.data.scripts.world;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import tecrys.data.scripts.world.subsidiaries.FreitagHQ;

public class ommGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
//        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("freitag_corporation");
//        initFactionRelationships(sector);
        (new FreitagHQ()).generate(sector);
    }
}
