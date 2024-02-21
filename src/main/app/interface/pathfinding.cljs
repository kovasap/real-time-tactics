(ns app.interface.pathfinding
  (:require [astar.core :refer [route]]
            [app.interface.gridmap
             :refer
             [get-tiles
              has-viewable-player-character?
              get-characters-current-tile
              get-adjacent-tiles
              get-characters-current-intention-tile]]
            [app.interface.character-stats
             :refer
             [get-steps-left-to-move get-insight]]
            [app.interface.attacking :refer [tile-in-attack-range?]]))

(def impassible 100)

(defn get-steps-to-move-to
  [{{:keys [aspects]} :land :keys [character-full-name] :as tile}
   {:keys [affinities]}]
  (cond character-full-name impassible ; cannot move to a tile with a
                                       ; character!
        (every? (fn [[aspect value]] (>= (aspect affinities) value)) aspects) 1
        :else impassible))

(defn get-number-of-path-steps
  [path character]
  (reduce + (for [tile (rest path)] (get-steps-to-move-to tile character))))

(defn gridmap->astar-args
  [gridmap character]
  {:h     (into {} (for [tile (get-tiles gridmap)] [tile 0]))
   :graph (into {}
                (for [tile (get-tiles gridmap)]
                  [tile (get-adjacent-tiles gridmap tile)]))
   :dist  (fn [_ to-tile] (get-steps-to-move-to to-tile character))})


(def get-path
 "Returns list of tiles in visited order."
 (memoize
  (fn [gridmap start-tile end-tile character]
   (let [{:keys [graph h dist]} (gridmap->astar-args gridmap character)]
    (vec (conj (route graph dist h start-tile end-tile) start-tile))))))


(defn truncate-path
  "Takes away tiles from the end of the path until it is under steps."
  [path steps character]
  (if (>= steps (get-number-of-path-steps path character))
    (vec path)
    (truncate-path (butlast path) steps character))) 

(defn truncate-occupied-path-steps
  [path]
  (if (:intention-character-full-name (last path))
    (truncate-occupied-path-steps (butlast path))
    (vec path)))

(defn get-path-usable-by-character
  "Truncate the given path so that the given character can actually take it on
  turn end."
  [path character]
  (truncate-occupied-path-steps
    (truncate-path path (get-steps-left-to-move character) character)))

(defn get-visible-player-character-tiles
  [gridmap viewing-character characters-by-full-name]
  (get-tiles gridmap
             #(has-viewable-player-character? %
                                              viewing-character
                                              characters-by-full-name)))

(defn get-path-to-nearest-player-character
  [gridmap character characters-by-full-name]
  (first
    (sort-by
      count
      (for [player-character-tile (get-visible-player-character-tiles
                                    gridmap
                                    character
                                    characters-by-full-name)]
        (get-path gridmap
                  (get-characters-current-tile gridmap character)
                  player-character-tile
                  character)))))

(defn get-usable-path-to-nearest-player-character
  [gridmap character characters-by-full-name]
  (get-path-usable-by-character
    (get-path-to-nearest-player-character gridmap
                                          character
                                          characters-by-full-name)
    character))

(defn get-usable-path-to-nearest-attackable-player-character
  [gridmap character characters-by-full-name]
  (let [candidate-path (get-usable-path-to-nearest-player-character
                         gridmap
                         character
                         characters-by-full-name)
        final-location (last candidate-path)]
    (if (empty?
          (get-tiles gridmap
                     #(and (tile-in-attack-range? character final-location %)
                           (has-viewable-player-character?
                             %
                             character
                             characters-by-full-name))))
      []
      candidate-path)))
