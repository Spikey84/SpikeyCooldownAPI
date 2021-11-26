package me.spikey.spikeycooldownapi.utils;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collections;
import java.util.List;

public class PermissionUtils {

    public static int getNumberedPermissionValueMin(String perm, Player player) {
        List<Integer> ints = Lists.newArrayList();



        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(perm)) {
                if (attachmentInfo.getPermission().equals(perm)) continue;
                ints.add(Integer.parseInt(attachmentInfo.getPermission().substring(attachmentInfo.getPermission().lastIndexOf(".") + 1)));
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
