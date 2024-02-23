(ns app.interface.pathfinding
  (:require [astar.core :refer [route]]
            [app.interface.gridmap
             :refer
             [get-tiles
              get-characters-current-tile
              get-adjacent-tiles]]
            [app.interface.constant-game-data :refer [weapons]]
            [app.interface.character-stats
             :refer
             [get-steps-left-to-move get-insight]]))

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

(defn get-path-usable-by-character
  "Truncate the given path so that the given character can actually take it on
  turn end."
  [path character]
  (truncate-path path (get-steps-left-to-move character) character))

; TODO expand this
(defn has-viewable-player-character?
  [tile viewing-character]
  (:character-full-name tile))

(defn get-visible-player-character-tiles
  [gridmap viewing-character]
  (get-tiles gridmap #(has-viewable-player-character? % viewing-character)))
                                              

(defn get-path-to-nearest-player-character
  [gridmap character]
  (first
    (sort-by
      count
      (for [player-character-tile (get-visible-player-character-tiles
                                    gridmap
                                    character)]
                                    
        (get-path gridmap
                  (get-characters-current-tile gridmap character)
                  player-character-tile
                  character)))))

(defn get-usable-path-to-nearest-player-character
  [gridmap character]
  (get-path-usable-by-character
    (get-path-to-nearest-player-character gridmap character)
    character))


(defn distance
  [{from-row-idx :row-idx from-col-idx :col-idx}
   {to-row-idx :row-idx to-col-idx :col-idx}]
  (+ (abs (- from-row-idx to-row-idx))
     (abs (- from-col-idx to-col-idx))))

(defn get-attack-range
  [{:keys [equipped-weapon]}]
  (:range (equipped-weapon weapons)))

(defn tile-in-attack-range?
  [character character-tile tile]
  (> (inc (get-attack-range character))
     (distance character-tile tile)
     0))

(defn get-usable-path-to-nearest-attackable-player-character
  [gridmap character]
  (let [candidate-path (get-usable-path-to-nearest-player-character gridmap
                                                                    character)
        final-location (last candidate-path)]
    (if (empty?
          (get-tiles gridmap
                     #(and (tile-in-attack-range? character final-location %)
                           (has-viewable-player-character? % character))))
      []
      candidate-path)))
