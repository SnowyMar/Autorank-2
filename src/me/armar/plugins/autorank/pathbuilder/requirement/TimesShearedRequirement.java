package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

public class TimesShearedRequirement extends Requirement {

    int timesShorn = -1;

    @Override
    public String getDescription() {
        String lang = Lang.TIMES_SHEARED_REQUIREMENT.getConfigValue(timesShorn + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final int progressBar = this.getStatsPlugin().getNormalStat(StatsHandler.statTypes.TIMES_SHEARED,
                player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld()));

        // progress = progress.concat(progressBar + "/" + timesSheared);
        return progressBar + "/" + timesShorn;
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        if (!getStatsPlugin().isEnabled())
            return false;

        return this.getStatsPlugin().getNormalStat(StatsHandler.statTypes.TIMES_SHEARED, player.getUniqueId(),
                AutorankTools.makeStatsInfo("world", this.getWorld())) >= timesShorn;
    }

    @Override
    public boolean setOptions(final String[] options) {

        try {
            timesShorn = Integer.parseInt(options[0]);
        } catch (final Exception e) {
            return false;
        }

        return timesShorn != -1;
    }
}
