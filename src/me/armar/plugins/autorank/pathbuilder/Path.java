package me.armar.plugins.autorank.pathbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.result.Result;

/**
 * Represents a path that a player can take, including all requirements and
 * results.
 * <p>
 * Date created: 14:23:30 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class Path {

    // The display name is the name that will be shown to the player.
    // The internal name is used to find the properties of the path in paths.yml
    private String displayName = "", internalName = "";

    private final Autorank plugin;
    // A requirements holder is a holder for one or more requirements that can
    // be met simultaneously.
    private List<RequirementsHolder> prerequisites = new ArrayList<>();

    private List<RequirementsHolder> requirements = new ArrayList<RequirementsHolder>();

    private List<Result> results = new ArrayList<Result>();
    
    private List<Result> resultsUponChoosing = new ArrayList<Result>();

    public Path(final Autorank plugin) {
        this.plugin = plugin;
    }

    public void addPrerequisite(final RequirementsHolder prerequisite) {
        this.prerequisites.add(prerequisite);
    }

    public void addRequirement(final RequirementsHolder requirement) {
        requirements.add(requirement);
    }

    public void addResult(Result result) {
        this.results.add(result);
    }

    public boolean applyChange(final Player player) {
        boolean result = true;

        if (meetRequirements(player)) {

            final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

            // Apply all 'main' results

            // Get chosen path of player
            Path currentPath = plugin.getPathManager().getCurrentPath(uuid);

            if (currentPath == null) {
                return false;
            }

            // Player already got this path
            if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, currentPath.getInternalName())) {
                return false;
            }

            // Add progress of completed requirements
            plugin.getPlayerDataConfig().addCompletedPath(uuid, currentPath.getInternalName());
            
            // Remove path from started paths if it's completed.
            plugin.getPlayerDataConfig().removeStartedPath(uuid, currentPath.getInternalName());

            for (final Result r : this.getResults()) {
                if (r != null) {
                    if (!r.applyResult(player)) {
                        result = false;
                    }
                }
            }

            // After getting results, chosen path is reset.
            plugin.getPlayerDataConfig().setChosenPath(uuid, null);

            // Reset progress
            plugin.getPlayerDataConfig().setCompletedRequirements(uuid, null);

        } else {
            result = false;
        }

        return result;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<RequirementsHolder> getFailedRequirementsHolders(final Player player) {
        final List<RequirementsHolder> holders = new ArrayList<RequirementsHolder>();

        for (final RequirementsHolder holder : this.getRequirements()) {
            if (holder != null)
                if (holder.meetsRequirement(player, player.getUniqueId(), false)) {
                    holders.add(holder);
                }
        }

        return holders;
    }

    public List<RequirementsHolder> getPrerequisites() {
        return prerequisites;
    }

    public List<RequirementsHolder> getRequirements() {
        return requirements;
    }

    public List<Result> getResults() {
        return results;
    }

    public boolean meetRequirements(final Player player) {

        UUID uuid = player.getUniqueId();
        
        // Get chosen path of player
        Path currentPath = plugin.getPathManager().getCurrentPath(uuid);

        if (currentPath == null) {
            return false;
        }

        // Player already completed this path
        if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, currentPath.getInternalName())) {
            return false;
        }

        for (final RequirementsHolder holder : this.getRequirements()) {
            if (holder == null)
                return false;

            // We don't do partial completion so we only need to check if a
            // player passes all requirements holders.
            if (!plugin.getConfigHandler().usePartialCompletion()) {
                if (!holder.meetsRequirement(player, uuid, false)) {
                    return false;
                } else {
                    continue;
                }
            }

            // Holder does not meet requirements, so not all requirements are
            // met!
            if (!holder.meetsRequirement(player, uuid, false)) {
                return false;
            }

        }

        // When never returning false, return true at last!
        return true;
    }

    public boolean meetsPrerequisites(Player player) {

        List<RequirementsHolder> preRequisites = this.getPrerequisites();

        for (RequirementsHolder preRequisite : preRequisites) {
            if (!preRequisite.meetsRequirement(player, player.getUniqueId(), false)) {
                // If one of the prerequisites does not hold, a player does not
                // meet all the prerequisites.
                return false;
            }
        }

        return true;
    }
    
    /**
     * Perform the results upon choosing this path.
     * @param player Player to perform them for.
     * @return true if all results were performed succesfully, false otherwise.
     */
    public boolean performResultsUponChoosing(Player player) {
        boolean success = true;
        
        for (Result r: this.getResultsUponChoosing()) {
            if (!r.applyResult(player)) {
                success = false;
            }
        }
        
        return success;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRequirements(final List<RequirementsHolder> holders) {
        this.requirements = holders;
    }

    public void setResults(final List<Result> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public List<Result> getResultsUponChoosing() {
        return resultsUponChoosing;
    }

    public void setResultsUponChoosing(List<Result> resultsUponChoosing) {
        this.resultsUponChoosing = resultsUponChoosing;
    }
}
