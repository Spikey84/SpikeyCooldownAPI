package me.spikey.spikeycooldownapi.utils;

import com.google.common.collect.Lists;
import me.spikey.spikeycooldownapi.Main;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PermissionUtils {

    public static int getNumberedPermissionValueMin(String perm, UUID uuid) {
        List<Integer> ints = Lists.newArrayList();

        Collection<PermissionNode> nodes = Main.getLuckPerms().getUserManager().getUser(uuid).resolveInheritedNodes(NodeType.PERMISSION, QueryOptions.nonContextual());

        List<String> perms = Lists.newArrayList();

        for (Node node : nodes) {
            perms.add(node.getKey());
        }

        for (String p : perms) {
            if (p.startsWith(perm)) {
                if (p.equals(perm)) continue;
                ints.add(Integer.parseInt(p.substring(p.lastIndexOf(".") + 1)));
            }
        }

        if (ints.size() == 0) {
            return 0;
        }

        List<Integer> intsSorted = Lists.newArrayList(ints);

        Collections.sort(intsSorted);

        return intsSorted.get(0);
    }

    public static int getNumberedPermissionValueMax(String perm, Player player) {
        List<Integer> ints = Lists.newArrayList();



        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(perm)) {
                if (attachmentInfo.getPermission().equals(perm)) continue;
                ints.add(Integer.parseInt(attachmentInfo.getPermission().substring(attachmentInfo.getPermission().lastIndexOf(".") + 1)));
            }
        }

        List<Integer> intsSorted = Lists.newArrayList(ints);

        Collections.sort(intsSorted);

        return intsSorted.get(intsSorted.size()-1);
    }
}
