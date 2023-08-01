package tecrys.data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import tecrys.data.scripts.world.ommGen;
//import exerelin.campaign.SectorManager;
import exerelin.campaign.SectorManager;

public class FreitagCorporation_ModPlugin extends BaseModPlugin {

    public static final boolean isExerelin;

    static {
        boolean foundExerelin;
        if (Global.getSettings().getModManager().isModEnabled("nexerelin")) {
            foundExerelin = true;
        } else {
            foundExerelin = false;
        }
        isExerelin = foundExerelin;
    }

    @Override
    public void onNewGame() {
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getCorvusMode()) {
            new ommGen().generate(Global.getSector());
            

        }
    }
    @Override
    public void onNewGameAfterEconomyLoad(){
                            MarketAPI market = Global.getSector().getEconomy().getMarket("eldfell"); //to get the market 
                    if (market != null) {
        market.addSubmarket("freitag_submarket"); //add the submarket into the market
            }
                            MarketAPI market2 = Global.getSector().getEconomy().getMarket("new_maxios"); //to get the market 
                    if (market2 != null) {
        market2.addSubmarket("freitag_submarket"); //add the submarket into the market
            }
                            MarketAPI market3 = Global.getSector().getEconomy().getMarket("ilm"); //to get the market 
                    if (market3 != null) {
        market3.addSubmarket("freitag_submarket"); //add the submarket into the market
            }
                                                MarketAPI market4 = Global.getSector().getEconomy().getMarket("freitag_hq"); //to get the market 
                    if (market4 != null) {
        market4.addSubmarket("freitag_submarket"); //add the submarket into the market
            }
    }
}
