(ns app.interface.character-stats 
  (:require
    [re-frame.core :as rf]))

; TODO on leveling, have each character gain levels / experience in each
; affinity proportional to the number of tiles of that land type they traversed
; and faught on since their last level

(defn calc-insight
  [affinities]
  (* 1 (:light affinities)))

(defn get-insight
  [character]
  (calc-insight (:affinities character)))

(defn can-view-character-intention?
 [viewing-character character]
 (>= (get-insight viewing-character) (get-insight character)))

(defn calc-defense
  [affinities]
  (* 1 (:water affinities)))

(defn calc-max-health
  [affinities]
  (* 5 (:earth affinities)))

(defn calc-speed
 [affinities]
 (* 1 (:air affinities)))

(defn calc-move-range
  [affinities]
  4)
 
(defn calc-power
 [affinities]
 (* 1 (:fire affinities)))

(defn calc-sneak
 [affinities]
 (* 1 (:dark affinities)))

(defn calc-ambition
 [affinities]
 (+ 1 (/ (:dark affinities) 4)))

; TODO make every class give a bonus to this value
(defn get-max-health
  [character]
  (calc-max-health (:affinities character)))

(defn get-health
  [{:keys [health]
    :as   character}]
  (if health health (get-max-health character)))

(defn get-speed
  [character]
  (calc-speed (:affinities character)))

(def experience-to-next-level 100)

(defn get-steps-left-to-move
  [{:keys [tiles-already-moved affinities]}]
  (- (calc-move-range affinities) tiles-already-moved))

(rf/reg-event-db
  :toggle-select-for-chapter
  (fn [db [_ full-name]]
    (update-in db [:characters full-name :selected-for-chapter?]
               not)))
