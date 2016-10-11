(ns clojureintro.core
  (:use bakery.core))

(def pantry-ingredients  #{:flour :sugar})

(def fridge-ingredients  #{:butter :egg :milk})

(def squeezed-ingredients #{:egg})
(def scooped-ingredients #{:flour :sugar :milk})
(def simple-ingredients #{:butter})

(defn from-panty?  [ingredient]
  (contains? pantry-ingredients ingredient))

(defn from-fridge?  [ingredient]
  (contains? fridge-ingredients ingredient))


(defn error [& rs]
  (apply println rs)
  :error)

(defn fetch-from-pantry
  ([ingredient]
   (fetch-from-pantry ingredient 1))
  ([ingredient amount]
   (if (from-panty? ingredient)
     (do
       (go-to :pantry)
       (println "I am at the panty")
       (dotimes [i amount]
         (load-up ingredient))
       (go-to :prep-area)
       (dotimes [i amount]
         (unload ingredient)))
     (error "This function only works on pantry ingredients. You passed " ingredient))))

(defn fetch-from-fridge
  ([ingredient]
   (fetch-from-fridge ingredient 1))
  ([ingredient amount]
   (if (from-fridge? ingredient)
     (do
       (go-to :fridge)
       (dotimes [i amount]
         (load-up ingredient))
       (go-to :prep-area)
       (dotimes [i amount]
         (unload ingredient)))
     (error "This function only works on fridge ingredients. You passed " ingredient))))

(defn fetch-ingregient
  ([ingredient]
   (fetch-ingregient ingredient 1))
  ([ingredient amount]
   (cond
     (from-panty? ingredient)
     (fetch-from-pantry ingredient amount)
     (from-fridge? ingredient)
     (fetch-from-fridge ingredient amount))
   :else
   (error "Huh?" ingredient)))

(defn add-egg []
  (grab :egg)
  (squeeze)
  (add-to-bowl))

(defn add-sugar []
  (grab :cup)
  (scoop :sugar)
  (add-to-bowl)
  (release))

(defn add-flour []
  (grab :cup)
  (scoop :flour)
  (add-to-bowl)
  (release))

(defn add-milk []
  (grab :cup)
  (scoop :milk)
  (add-to-bowl)
  (release))

(defn add-butter []
  (grab :butter)
  (add-to-bowl))

(defn scooped? [ingredient]
  (contains? scooped-ingredients ingredient))

(defn squeezed? [ingredient]
  (contains? squeezed-ingredients ingredient))

(defn simple? [ingredient]
  (contains? simple-ingredients ingredient))


(defn add-eggs [n]
  (dotimes [e n]
    (add-egg)))

(defn add-flour-cups [n]
  (dotimes [e n]
    (add-flour)))

(defn add-milk-cups [n]
  (dotimes [e n]
    (add-milk)))

(defn add-sugar-cups [n]
  (dotimes [e n]
    (add-sugar)))

(defn add-butters [n]
  (dotimes [e n]
    (add-butter)))

(defn add-squeezed
  ([ingredient]
   (add-squeezed ingredient 1))
  ([ingredient amount]
   (if (squeezed? ingredient)
     (dotimes [i amount]
       (grab ingredient)
       (squeeze)
       (add-to-bowl))
     (error (str "This function only works on squeezed ingredients. You asked me to squeeze " ingredient)))))

(defn add-scooped
  ([ingredient]
   (add-scooped ingredient 1))
  ([ingredient amount]
   (if (scooped? ingredient)
     (do
       (grab :cup)
       (dotimes [i amount]
         (scoop ingredient)
         (add-to-bowl))
       (release))
     (error (str "This function only works on scooped ingredients. You asked me to scoop " ingredient)))))

(defn add-simple
  ([ingredient]
   (add-simple ingredient 1))
  ([ingredient amount]
   (if (simple? ingredient)
     (dotimes [i amount]
       (grab ingredient)
       (add-to-bowl))
     (error (str "This function only works on simple ingredients. You asked me to add " ingredient)))))

(defn add
  ([ingredient]
   (add ingredient 1))
  ([ingredient amount]
   (cond
     (squeezed? ingredient)
     (add-squeezed ingredient amount)

     (simple? ingredient)
     (add-simple ingredient amount)

     (scooped? ingredient)
     (add-scooped ingredient amount)

     :else
     (do
       (println "I do not have the ingredient" ingredient)
       :error))))


(defn load-up-amount [ingredient amount]
  (dotimes [i amount]
    (load-up ingredient)))

(defn unload-amount [ingredient amount]
  (dotimes [i amount]
    (unload ingredient)))



(defn fetch-list [shopping-list]
  (doseq [[location ingredients] {:pantry pantry-ingredients
                                  :fridge fridge-ingredients}]
    (go-to location)
    (doseq [ingredient ingredients]
      (load-up-amount ingredient (ingredient shopping-list 0))))

  (go-to :prep-area)
  (doseq [[ingredient amount] shopping-list]
    (unload-amount ingredient (ingredient shopping-list 0))))


(defn bake-cake []
  (add :egg 2)
  (add :flour 2)
  (add :milk 1)
  (add :sugar 1)

  (mix)

  (pour-into-pan)
  (bake-pan 25)
  (cool-pan))

(defn bake-cookies []
  (add :egg 1)
  (add :flour 1)
  (add :butter 1)
  (add :sugar 1))

(defn  add-ingredients [shopping-list1 shopping-list2]
  (merge-with + shopping-list1 shopping-list2))

(defn multiply-ingredients [n-times ingredients]
  (into {} (for [[ ingredient amount ] ingredients]
             {ingredient (* n-times amount)})))

(defn order->ingregients [order]
  (add-ingredients (multiply-ingredients (:cake (:items order) 0)
                                         {:egg 2 :flour 3 :milk 1 :sugar 1})
                   (multiply-ingredients (:cookies (:items order) 0)
                                         {:egg 1 :flour 1 :butter 1 :sugar 1})))

(defn orders->ingredients [orders]
  (reduce add-ingredients (map order->ingregients orders)))

(defn day-at-the-bakery []
  (doseq [order (get-morning-orders)]
    ;; get a map of items in the order and look up the cakes count
    (dotimes [n (:cake (:item order) 0)]
      (fetch-list {:egg 2 :flour 3 :milk 1 :sugar 1})
      (delivery {:orderid (:orderid order)
                 :address (:address order)
                 :rackids [(bake-cake)]}))
    (dotimes [n (:cookies (:item order) 0)]
      (fetch-list {:egg 1 :flour 1 :butter 1 :sugar 1})
      (delivery {:orderid (:orderid order)
                 :address (:address order)
                 :rackids [(bake-cookies)]}))))

(defn -main []
  ;; (println (get-morning-orders))
  ;; (day-at-the-bakery)
  (orders->ingredients (get-morning-orders))
  )

(-main)

