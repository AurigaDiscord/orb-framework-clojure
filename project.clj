(defproject orb-framework "0.1.2-SNAPSHOT"
  :description "Auriga orb framework"
  :url "https://github.com/AurigaDiscord/orb-framework-clojure"
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/data.json "0.2.6"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.fzakaria/slf4j-timbre "0.3.12"]
                 [environ "0.5.0"]
                 [com.novemberain/langohr "5.0.0"]
                 [com.soundcloud/prometheus-clj "2.4.1"]
                 [ring-server "0.5.0"]
                 [compojure "1.6.1"]])
