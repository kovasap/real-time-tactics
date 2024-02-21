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


(defn make-move-intention
  [{:keys [full-name]} path gridmap]
  (if (> 2 (count path))
    gridmap
    (let [steps (subvec path 1 (count path))
          {to-row-idx :row-idx to-col-idx :col-idx} (last path)]
      (->
        gridmap
        ; Add waypoints
        ((apply comp
          (for [{path-row-idx :row-idx path-col-idx :col-idx} steps]
           #(assoc-in % [path-row-idx path-col-idx :waypoint-for] full-name))))
        (#(clear-intentions % full-name))
        (assoc-in [to-row-idx to-col-idx :intention-character-full-name]
                  full-name)))))

(defn update-move-intention
  "Returns a gridmap with :intention-character-full-name tile keys filled in."
  [{:keys [ai-behavior] :as character} characters-by-full-name gridmap]
  ((partial
    make-move-intention
    character
    ((ai-behavior ai-behaviors) gridmap character characters-by-full-name))
   gridmap))


(defn has-adjacent-enemy?
  [gridmap character]
  (filter
    ; TODO fix this condition
    #(not (= (:character-full-name %) (:full-name character)))
    (get-tiles-adjacent-to-character gridmap character)))

(defn determine-npc-action
  [gridmap {:keys [full-name] :as npc}]
  (cond
    (has-adjacent-enemy? gridmap npc) {:character-full-name full-name
                                       :action-type :attack}
    ; Move
    :else {:new-position []
           :character-full-name full-name
           :action-type :move}))

(rf/reg-event-fx
  :queue-npc-actions
  (fn [cofx _]
    {:fx (into []
               (for [npc @(rf/subscribe [:characters])]
                 [:dispatch
                  [:add-action-to-queue
                   (determine-npc-action @(rf/subscribe [:gridmap]) npc)]]))}))
