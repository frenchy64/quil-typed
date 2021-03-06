(ns quil.typed.examples.gen-art.13-noisy-spiral
  (:use quil.core
        [quil.helpers.drawing :only [line-join-points]]
        [quil.helpers.seqs :only [range-incl steps]]
        [quil.helpers.calc :only [mul-add]])
  (:require [quil.typed :as qt]
            [clojure.core.typed :as t]))

;; Example 13 - Noisy Spiral
;; Taken from Listing 4.3, p69

;; void setup() {
;;   size(500,300);
;;   background(255);
;;   strokeWeight(5);
;;   smooth();
;;   float radius = 100;
;;   int centX = 250;
;;   int centY = 150;
;;   stroke(0, 30);
;;   noFill();
;;   ellipse(centX,centY,radius*2,radius*2);
;;   stroke(20, 50, 70);

;;   radius = 10;
;;   float x, y;
;;   float lastx = -999;
;;   float lasty = -999;
;;   float radiusNoise = random(10);
;;   for(float ang=0;ang<=1440;ang+=5){
;;     radiusNoise += 0.05;
;;     radius += 0.5;
;;     float thisRadius = radius + (noise(radiusNoise) * 200) - 100;
;;     float rad = radians(ang);
;;     x = centX + (thisRadius * cos(rad));
;;     y = centY + (thisRadius * sin(rad));
;;     if (lastx > -999) {
;;       line(x,y,lastx,lasty);
;;     }
;;     lastx = x;
;;     lasty = y;
;;   }
;; }

(t/ann setup [-> Any])
(defn setup []
  (background 255)
  (stroke-weight 5)
  (smooth)
  (let [radius    100
        cent-x    250
        cent-y    150
        rad-noise (steps (rand 10) 0.05)
        rad-noise (map (t/ann-form #(* 200 (noise %)) 
                                   [Number -> Number]) 
                       rad-noise)
        rads      (map radians (range-incl 0 1440 5))
        radii     (steps 10 0.5)
        radii     (map (t/ann-form (fn [rad noise] (+ rad noise -100)) 
                                   [Number Number -> Number])
                       radii rad-noise)
        xs        (map (t/ann-form (fn [rad radius] (mul-add (cos rad) radius cent-x)) 
                                   [Number Number -> Number])
                       rads radii)
        ys        (map (t/ann-form (fn [rad radius] (mul-add (sin rad) radius cent-y)) 
                                   [Number Number -> Number])
                       rads radii)
        line-args (line-join-points xs ys)]
    (stroke 0 30)
    (no-fill)
    (ellipse cent-x cent-y (* radius 2) (* radius 2))
    (stroke 20 50 70)
    (t/tc-ignore
      (dorun (map #(apply line %) line-args)))))

(qt/ann-sketch gen-art-13)
(defsketch gen-art-13
  :title "Noisy Spiral"
  :setup setup
  :size [500 300])
