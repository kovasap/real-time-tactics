(ns app.interface.lands)

(def lands
  [{:letter        "F"
    :terrain       :forest
    :perlin-cutoff 0.35
    :aspects {:fire 0 :air 1 :earth 1 :water 1 :light 0 :dark 1}
    :style         {:background-color "forestgreen"}}
   {:letter        "P"
    :terrain       :plains
    :perlin-cutoff 0.3
    :aspects {:fire 1 :air 1 :earth 1 :water 1 :light 1 :dark 0}
    :style         {:background-color "LightGreen"}}
   {:letter        "W"
    :terrain       :water
    :perlin-cutoff 0.0
    :aspects {:fire 0 :air 0 :earth 0 :water 3 :light 0 :dark 0}
    :style         {:background-color "MediumTurquoise"}}
   {:letter        "M"
    :terrain       :mountain
    :perlin-cutoff 0.75
    :aspects {:fire 0 :air 1 :earth 2 :water 0 :light 0 :dark 0}
    :style         {:background-color "grey"}}
   {:letter        "S"
    :terrain       :sand
    :perlin-cutoff 0.2
    :aspects {:fire 2 :air 1 :earth 0 :water 0 :light 1 :dark 0}
    :style         {:background-color "yellow"}}
   {:letter        "C"
    :terrain       :wall
    :aspects {:fire 0 :air 0 :earth 2 :water 0 :light 0 :dark 1}
    :style         {:background-color "DimGrey"}}
   {:letter        "R"
    :terrain       :road
    :aspects {:fire 0 :air 0 :earth 0 :water 0 :light 1 :dark 0}
    :style         {:background-color "AntiqueWhite"}}
   {:letter        "B"
    :terrain       :bridge
    :aspects {:fire 0 :air 0 :earth 1 :water 0 :light 1 :dark 0}
    :style         {:background-color "Brown"}}
   {:letter        "V"
    :terrain       :void
    :perlin-cutoff 10.0
    :aspects {:fire 0 :air 0 :earth 0 :water 0 :light 0 :dark 3}
    :style         {:background-color "black"}}])
