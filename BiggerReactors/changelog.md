# 1.16.3-0.1.6
bug fixes

## Reactor
 - GUIs added for ports, only allow toggle
 - OutputMultiplier fixed for active reactors
 - Control rod levels apply after reload

## Turbine
 - GUIs added for coolant port, only allows toggle
 - Default turbine max flow rate changed to 5,000mB/t, and tank size to 10,000mB
 - Internal battery size adjusted by 2.5x to match above change

## Misc
 - fixed cyanite reprocessor voiding items
 - corrected the uranium to cyanite recipe

# 1.16.3-0.1.5
more tagging, fix for ATM6 dust smelting

## Misc
 - add ingots to forge:ingots
 - add dusts to forge:dusts
 - add yellorium_dust to forge:dusts/uranium_dust

# 1.16.3-0.1.4
tagging

## Reactor
 - Reactor accepts any forge:ingots/uranium
 - Glass reports errors correctly
 - Reactor will save generated power through reload
 - Added more moderators

## Turbine
 - Inductor drag config option fixed
 - Computerport will properly set vent all
 - Added inductors

## Misc
 - Cyanite reprocessor works with pipes properly
 - Yellorite is tagged as forge:ores/uranium, and forge:ores
 - AE2 grindstone recipes added
 - zh_cn translation added (thanks qsefthuopq)

# 1.16.3-0.1.3
Mekanism compatibility

## Reactor
 - Reactor can now produce Mekanism gas steam, just connect a Mekanism gas pipe to the coolant port

## Turbine
 - Turbine can now accept Mekanism gas steam, just connect a Mekanism gas pipe to the coolant port.
 - Computer Craft API bug fixed. Steam and water were being reported as each other.

## Misc
 - Mekanism Enrichment Chamber and Crusher compatibility added. Pretty much the same as the Pulverizer.
 - Computer ports have names in the lang file now.

# 1.16.3-0.1.2
wrench compat

## Reactor
 - Fixed wrench compat with mekanism
 - Control rods save their insertion

## Turbine
 - Fixed wrench compat with mekanism

# 1.16.3-0.1.1
computer ports and rotor rotation

## Reactor
 - Computer port implemented
## Turbine
 - Computer port implemented
 - Rotor will now rotate to the correct axis
 
## Misc
 - Added config option to disable oregen

# 1.16.3-0.1.0
fist 1.16 release, minor changes

## Reactor
 - Control rod has a GUI now
## Turbine
 - Flow rate change buttons work properly (bug discovered because of control rod)
## Misc
 - Yellorium bucket named fixed
 - Phosphophyllite separated into different jar, required for BiR (see Phosphophyllite changelog for details)

# 1.15.2-0.0.1
bug fix

## Reactor
 - blocks registered with tags not added to registry correct (thermal blocks)

# 1.15.2-0.0.0
initial version, changes from BR 0.4.3A

## Reactor
 - Access port must be pushed into, and does not have a gui nor internal buffer
 - Terminal GUI works a bit different, but not majorly, also called terminal now, not controller
 - Control rod does not have a GUI (yet)
 - Computer port does not exist (yet)

## Turbine
 - Terminal GUI works a bit different, but not majorly, also called terminal now, not controller
 - Turbine requires two bearings, one on each end
 - Computer port does not exist (yet)
 - You can't over speed turbines anymore, there is a soft limit to the speed, so long as you were under 2000 rpm it's the same
     - Small sub-note there, i did modify the efficiency function in the 450-500 RPM range to have a better transition, shouldn't really matter though
 - You now need to rotor bearings, one at each end, orientation of rotor doesn't matter
 
## Misc
 - There is a pile of stuff in the config now, sooo many internal values that were magic before
 

 
#### Beginning of time?
