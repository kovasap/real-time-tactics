(ns app.interface.view.gridmap
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [clojure.string :as st]
            [app.interface.view.character
             :refer
             [character-view]]
            [app.interface.config :refer [debug]]
            [app.interface.utils :refer [get-only]]))


(def tile-size "130px")


(defn tile-view
  [{:keys [land
           row-idx
           col-idx
           character-full-name
           waypoint-for]
    :as   tile}]
  (let [character (get-only @(rf/subscribe [:characters])
                            :full-name character-full-name)]
    [:div.tile
     {:style         {:font-size    "12px"
                      :text-align   "center"
                      :height       tile-size
                      :width        tile-size
                      :aspect-ratio "1"
                      :position     "relative"}}
     [:div.background
      {:style (merge (:style land)
                     {:width        "100%"
                      :height       "100%"
                      :position     "absolute"
                      :z-index      -1})}]
     [:div {:style {:position "absolute" :padding-top "10px" :width "100%"}}
      [:div {:style {:display (if debug "block" "none")}}
       row-idx
       ", "
       col-idx]
      (if waypoint-for [:span "wp"] nil)
      (if character
        [character-view character])]]))


(defn tile-info-view
  [{:keys [row-idx col-idx] {:keys [terrain aspects]} :land}]
  [:div
   [:p row-idx ", " col-idx]
   [:p (name terrain)]
   [:p (str aspects)]])


(defn gridmap-view
  [gridmap]
  (into [:div.gridmap
         {:style {:display  "grid"
                  :grid-template-columns (st/join " "
                                                  (for [_ (first gridmap)]
                                                    tile-size))
                  :grid-gap "1px"}}]
        (reduce concat
          (for [column gridmap] (for [tile column] (tile-view tile))))))
