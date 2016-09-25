(ns lymchat.android.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [lymchat.handlers]
            [lymchat.subs]
            [lymchat.utils.module :as m]
            [lymchat.utils.assets :as assets]
            [lymchat.android.router :as router]
            [lymchat.constants :as lc]))

(aset js/console "disableYellowBox" true)

(defn status-bar-cp []
  (cond
    (m/ios?)
    [m/status-bar {:bar-style "default"}]

    (m/android?)
    [m/view {:style (:statusBarUnderlay lc/styles)}]

    :else
    nil))

(defn app-root []
  (r/create-class
   {:component-will-mount (fn []
                            (assets/cache-assets []
                                                 [{"space-mono" (js/require "./assets/fonts/SpaceMono-Regular.ttf")}]
                                                 #(dispatch [:set-app-ready? true])))
    :reagent-render
    (fn []
      (let [app-ready? (subscribe [:app-ready?])]
        (if @app-ready?
          [m/view {:style {:flex 1
                           :background-color "#FFF"
                           :margin-top 24}}
           [m/navigation-provider {:router router/router}
            [m/stack-navigation {:id "root"
                                 :initialRoute (.getRoute router/router "home")}]]
           [status-bar-cp]]

          [m/app-loading])))}))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent m/app-registry "main" #(r/reactify-component app-root)))