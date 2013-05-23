(ns katybot.file-memory
  (:use [katybot.core]))

(defn- read-file [file]
  (if (.exists (clojure.java.io/as-file file))
    (read-string (slurp file))
    {}))

(defn- update-state [state file memory]
  (spit file memory)
  (assoc state :memory memory))

(defn- store [state k v]
  (let [{file :file, memory :memory} state]
    (update-state state file (assoc memory k v))))

(defn- remove [state k]
  (let [{file :file, memory :memory} state]
    (update-state state file (dissoc memory k))))

(defn +file-memory [robot file]
  (assoc robot
    :memory ::file-memory 
    ::agent (agent {:file file :memory (read-file file)})))

(defmethod memorize ::file-memory [robot k v]
  (send (::agent robot) store k v)
  robot)

(defmethod recall ::file-memory [robot k]
  (get-in @(::agent robot) [:memory k]))

(defmethod forget ::file-memory [robot k]
  (swap! (::storage robot) remove k)
  robot)
