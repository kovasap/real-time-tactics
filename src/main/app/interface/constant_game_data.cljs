(ns app.interface.constant-game-data)

(def weapon-advantages
  {:sword-and-shield #{:spear :cloak-and-dagger}
   :spear #{:axe}
   :axe #{:sword-and-shield}
   :cloak-and-dagger #{:spear}
   :staff #{}
   :flail #{:sword-and-shield}})

(def weapons
  {:sword-and-shield {:image "item-images/buckler.png" :range 1 :damage 6}
   :axe {:image "item-images/axe.png" :range 1 :damage 8}
   :spear {:image "item-images/spear.png" :range 1 :damage 5}
   :flail {:image "item-images/sling.png" :range 1 :damage 4}
   :cloak-and-dagger {:image "item-images/dagger.png" :range 1 :damage 2}
   :staff {:image "item-images/staff.png" :range 1 :damage 6}
   :tome {:image "item-images/book1.png" :range 1 :damage 6}})

(def utility-items
  {:smoke-bomb {}
   :health-potion {}})

(def character-classes
  ; Images taken from wesnoth/data/core/images/units/human-peasants/peasant-
  {:skirmisher
   {:equippable-weapons #{:sword-and-shield :spear}
    :prerequisite-affinities {:fire 1 :earth 1 :air 1 :water 1}
    ; Affinites that can be leveled as this class
    :advancement-affinities {:fire 1 :earth 1}
    ; TODO automatically determine these based on the files in the
    ; resources/public/class-images/<class-name> directories
    :animation-frames {:attack 5}
    :equippable-utility #{:smoke-bomb :health-potion}}
   :scholar
   {:equippable-weapons #{:tome}
    :prerequisite-affinities {:light 1 :dark 1 :water 3}
    :advancement-affinities {:light 1 :water 1}
    :animation-frames {:attack 2}
    :equippable-utility #{:smoke-bomb}}
  ; TODO make it so that this class can create bone minions
   :necromancer
   {:equippable-weapons #{:cloak-and-dagger}
    :prerequisite-affinities {:earth 1 :dark 3}
    :advancement-affinities {:dark 1 :earth 1}
    :animation-frames {:attack 5}
    :equippable-utility #{:smoke-bomb}}
  ; TODO make it so that this class can scale walls and get bonus damage on
  ; opponents next to other friendly characters (flanked)
   :assassin
   {:equippable-weapons #{:cloak-and-dagger :crossbow}
    :prerequisite-affinities {:air 2 :fire 1 :dark 2}
    :advancement-affinities {:dark 1 :air 1}
    :animation-frames {:attack 2}
    :equippable-utility #{:smoke-bomb :poison-bomb}}
  ; TODO make it so this class can grow trees to block off tiles
   :druid
   {:equippable-weapons #{:staff}
    :prerequisite-affinities {:water 2 :earth 3}
    :advancement-affinities {:earth 1 :water 1}
    :animation-frames {:attack 5}
    :equippable-utility #{}}
   :paladin
   {:equippable-weapons #{:sword-and-shield :spear}
    :prerequisite-affinities {:light 5 :fire 2}
    :advancement-affinities {:light 1 :fire 1}
    :animation-frames {:attack 2}
    :equippable-utility #{}}
   ; TODO make it so this class can heal
   :priest
   {:equippable-weapons #{:staff}
    :prerequisite-affinities {:light 1}
    :advancement-affinities {:light 1}
    :animation-frames {:attack 5}
    :equippable-utility #{}}})
