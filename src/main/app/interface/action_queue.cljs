(ns app.interface.action-queue
  (:require [re-frame.core :as rf]
            [app.interface.gridmap :refer [update-tiles]]))

(rf/reg-event-db
  :add-action-to-queue
  (fn [db [_ action]]
    (update db :action-queue #(conj % action))))

(defn execute-move-action
  [{:keys [character-full-name] [row-idx col-idx] :new-position} db]
  (update db
          :gridmap
          (fn [gridmap]
            (-> gridmap
                ; remove old positions for moved characters
                (update-tiles #(= (:character-full-name %) character-full-name)
                              #(dissoc % :character-full-name))
                ; add new positions
                (assoc-in [row-idx col-idx :character-full-name]
                          character-full-name)))))

(defn execute-attack-action
  [action db])

(rf/reg-event-db
  :execute-actions
  (fn [db _]
    (-> db
      ; Execute all actions
      ((apply comp
        ; TODO sort these in some way so there is logic to how they are executed.
        (for [{:keys [action-type] :as action} (:action-queue db)]
         (partial (case action-type
                    :move execute-move-action
                    :attack execute-attack-action)
                  action))))
      ; Clear queue
      (assoc :action-queue '()))))
