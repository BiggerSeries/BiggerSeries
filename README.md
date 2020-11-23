# BiggerSeries
The BiggerSeries is a series of 1 mod and 1 library mod (yes I want to do more) with one main goal with its machines, bigger than everything else.

## BiggerReactors
BiggerReactors is the official continuation of [Big Reactors](https://github.com/erogenousbeef-zz/BigReactors) by Erogenous Beef.
Anything you know and love about BigReactors is probably still true with BiggerReactors, as the modifications are minor.

Downloads can be found in the GitHub releases tab and on CurseForge [here](https://www.curseforge.com/minecraft/mc-mods/biggerreactors)

### Notable changes
 - GUI button are toggle in place, instead of discrete.
 - Reactor access ports no longer have an intenral buffer, required items to be piped in/out (this is mostly me being lazy, but you should be doing this anyway, hopper works).
 - Overspeeding turbines no longer generates more power, the efficiency function is nerfed past about 2200RPM. They still don't break, but you have no reason to run them that fast.
 - Turbine require two rotor bearings, one at each end. Small annoyance on my part, but otherwise the rotors work the same, put the coils on either side.
 - The config can edit basically everything, re-balance the reactor to your hearts content as a modpack dev.
 - FE is the only supported energy system, but its about as widely used now as RF was then, so, shouldn't notice the change.
 -  Reactors and Turbines now have dynamic energy buffers to support much larger reactors and turbines that could generate more than the full buffer in one tick. (CC API scales the amount as if it was a 10MiRF/1MiRF buffer, use the new methods to get actual info)
 - Reactors no longer have a steam production limit, built them as big as you like
 - Turbines are not limited to 2B/t, they automatically adjust their limits based on the number of blades you have
 - The default max size limits have changed, Reactors may be built up to 64x64x96, turbines up to 32x32x192
 
 
## You need other mods to use it
Much like Big Reactors before it, BiggerReactors is a power generation mod *only.* You will need another mod to transport or the power.
Dont expect to be doing much with BiggerReactors by itself.
 
 
## Phosphophyllite
Phosphophyllite is my BeefCore, it's the library mod that has my multiblock API among other things in it.

Nothing much interesting here unless you are a mod dev, in which case, read the Readme in the Phosphophyllite directory, if it exists, may not yet.

Oh, right, downloads, again on GitGub releases tab or CurseForge [here](https://www.curseforge.com/minecraft/mc-mods/phosphophyllite).

## Beta
There are two people that work on these mods as of writing, and I (RogueLogix) am responsible for most of the mods functions.
Before you ask, its GUIs, that's what Gizmocodes does, if its not GUIs, its probably me.
With that said we can't possibly break everything that all of you will, so expect to find bugs sometimes. This *is* a full from scratch rewrite of Big Reactors.
Once we get to a point where there aren't any significant known bugs, and the unknown ones seem to be hiding themselves well, I may make a release build.

#### Note on old BigReactors bugs
Bugs from BigReactors are *probably* no longer valid, if you think it might be, verify that the bug still exists with BiggerReactors before reporting it.
This being a full rewrite it shares almost no code with BigReactors and as a result will have its own suite of bugs.

## Licence
I am personally *not* a fan of closed source mods, so, its not closed source. BiggerReactors is under the same MIT license as BigReactors.

This means that you are free to
 - Use it in a modpack (please do)
 - PR bug fixes and changes in (*please do*)
 - Redistribute it (please dont)
 - Take the source code and make it your own (if im not updating it, by all means)
 - Take now your code closed source

Just make sure to read LICENSE.txt, its a pretty non-restrictive license.
\
\
\
\
Why are you still here, there isn't anything left? Go to CurseForge and download it already, *you know you want to.*