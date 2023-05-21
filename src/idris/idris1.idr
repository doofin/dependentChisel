module Main

data Na    = Z   | S Na           -- Natural numbers

twoplustwo_eq_four : 2 + 2 = 4
twoplustwo_eq_four = Refl

plus : Na -> Na -> Na
plus Z = ?b_0
plus (S x) = ?b_1

isSingleton : Bool -> Type
isSingleton True = Nat
isSingleton False = List Nat

mkSingle : (x : Bool) -> isSingleton x
mkSingle True = 0
mkSingle False = []

-- wrapper for Nat, required!

data FinN : Nat -> Type where
  Fin : (n : Nat) ->  FinN n

add1: FinN n -> FinN (n+1) 
add1 (Fin n) = Fin (n+1)

fin0 : FinN 0
fin0 = Fin 0

-- f6: 6
-- add3:{n:Na}  ->  n -> FinN (S n)

data Vect : Nat -> Type -> Type where
   Nil  : Vect Z a
   (::) : a -> Vect k a -> Vect (S k) a

-- incorrect map for list , showing potential errors 
map : (a -> b) -> List a -> List b
map f []        = []
map f (x::y :: xs) =  [f x, f y]
map f (x :: xs) =  [] -- f x :: map f xs

-- map for vectors 
mapV : (a -> b) -> Vect n a -> Vect n b
mapV f []        = []
mapV f (x :: xs) = f x :: mapV f xs