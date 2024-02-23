(ns app.interface.core
  (:require ["react-dom/client" :refer [createRoot]]
            [day8.re-frame.http-fx]
            [day8.re-frame.undo :as undo :refer [undoable]]  
            [goog.dom :as gdom]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [app.interface.sente :refer [send-state-to-server!]]
            [app.interface.initial-db :refer [initial-db]]
            [app.interface.view.main :refer [main]]
            [cljs.pprint]
            [taoensso.timbre :as log]
            ; Need to import things we don't use here so that their re-frame
            ; stuff is seen.
            [app.interface.action-queue]
            [app.interface.npc-ai]
            [app.interface.keybinds]))

;; ----------------------------------------------------------------------------
;; Setup

(rf/reg-event-db
  :app/setup
  (fn [db _]
    initial-db))

(rf/reg-event-db
  :message
  (undoable "Send message")
  (fn [db [_ message]]
    (assoc db :message message)))

(doseq [kw [:gridmap :message :characters]]
  (rf/reg-sub
    kw
    (fn [db _] (kw db))))

;; -- Core Loop ---------------------------------------------------------------

(rf/reg-event-fx
  :end-turn
  (fn [cofx _]
    {:fx [[:dispatch [:queue-npc-actions]]
          [:dispatch [:execute-actions]]]}))

; See
; https://github.com/Day8/re-frame/blob/master/docs/FAQs/PollADatabaseEvery60.md
(js/setInterval #(rf/dispatch [:end-turn]) 1000)

;; -- Entry Point -------------------------------------------------------------

(defonce root (createRoot (gdom/getElement "app")))

(defn init
  []
  (rf/dispatch [:app/setup])
  (.render root (r/as-element [main])))

(defn- ^:dev/after-load re-render
  "The `:dev/after-load` metadata causes this function to be called after
  shadow-cljs hot-reloads code. This function is called implicitly by its
  annotation."
  []
  (rf/clear-subscription-cache!)
  (init))
