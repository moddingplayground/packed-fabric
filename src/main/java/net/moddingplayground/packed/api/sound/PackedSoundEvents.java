package net.moddingplayground.packed.api.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.packed.api.Packed;

public interface PackedSoundEvents {
    SoundEvent ITEM_BACKPACK_EQUIP = backpack("equip");
    private static SoundEvent backpack(String id) {
        return item("backpack", id);
    }

    SoundEvent ENCHANTMENT_DEFLECTION_HIT = deflection("hit");
    private static SoundEvent deflection(String id) {
        return enchantment("deflection", id);
    }

    private static SoundEvent item(String item, String id) {
        return register("item.%s.%s".formatted(item, id));
    }

    private static SoundEvent enchantment(String enchantment, String id) {
        return register("enchantment.%s.%s".formatted(enchantment, id));
    }

    private static SoundEvent register(String id) {
        Identifier identifier = new Identifier(Packed.MOD_ID, id);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }
}
