(ns app.interface.npc-ai 
  (:require
    [re-frame.core :as rf]
    [app.interface.gridmap :refer [get-tiles-adjacent-to-character]]
    [app.interface.pathfinding
     :refer
     [get-usable-path-to-nearest-player-character
      get-usable-path-to-nearest-attackable-player-character
      get-path-usable-by-character]]))


; TODO add "aggresive" "cautious" and other "personalities" to the AI movement
; potentially depending on their element affinity.

; Map of keywords to functions that take in a
; [gridmap character characters-by-full-name] and return a path the AI
; character will take.
(def ai-behaviors
  {; Always run at the nearest player character and try to attack, no matter
   ; how far away they are.
   :aggressive      get-usable-path-to-nearest-player-character
   ; Only move when an attack is possible from the newly moved to location.
   :attack-in-range get-usable-path-to-nearest-attackable-player-character})


(defn has-adjacent-enemy?
  [gridmap character]
  (filter
    ; TODO fix this condition
    #(not (= (:character-full-name %) (:full-name character)))
    (get-tiles-adjacent-to-character gridmap character)))

(defn determine-npc-action
  [gridmap {:keys [full-name ai-behavior] :as npc}]
  (cond
    ; Attack
    (has-adjacent-enemy? gridmap npc) {:character-full-name full-name
                                       :action-type         :attack}
    ; Move
    :else                             {:new-position        (last
                                                              ((ai-behavior
                                                                ai-behaviors)
                                                               gridmap
                                                               npc))
                                       :character-full-name full-name
                                       :action-type         :move}))

(rf/reg-event-fx
  :queue-npc-actions
  (fn [cofx _]
    {:fx (into []
               (for [npc @(rf/subscribe [:characters])]
                 [:dispatch
                  [:add-action-to-queue
                   (determine-npc-action @(rf/subscribe [:gridmap]) npc)]]))}))
