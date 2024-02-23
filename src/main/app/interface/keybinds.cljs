(ns app.interface.keybinds
  (:require [keybind.core :as key]
            [re-frame.core :as rf]
            [app.interface.initial-db :refer [player-character]]
            [app.interface.gridmap
             :refer
             [clamp-coordinates get-characters-current-tile]]))

(defn make-player-character-move-action
  [gridmap direction]
  {:character-full-name (:full-name player-character)
   :action-type         :move
   :new-position        (let [{:keys [row-idx col-idx]}
                              (get-characters-current-tile gridmap
                                                           player-character)]
                          (clamp-coordinates
                            gridmap
                            (case direction
                              :up    [(dec row-idx) col-idx]
                              :down  [(inc row-idx) col-idx]
                              :left  [row-idx (dec col-idx)]
                              :right [row-idx (inc col-idx)])))})

(rf/reg-event-fx
  :move-single-tile
  (fn [{:keys [db] :as cofx} [_ direction]]
    {:fx [[:dispatch
           [:add-action-to-queue
            (make-player-character-move-action (:gridmap db) direction)]]]}))

(key/bind! "up" ::up #(rf/dispatch [:move-single-tile :up]))
(key/bind! "down" ::down #(rf/dispatch [:move-single-tile :down]))
(key/bind! "left" ::left #(rf/dispatch [:move-single-tile :left]))
(key/bind! "right" ::right #(rf/dispatch [:move-single-tile :right]))
