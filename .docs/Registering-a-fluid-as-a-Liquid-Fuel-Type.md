> [!CAUTION] <h1>
> **This is 1.6.0 Feature**

## How to add a new type
Adding a custom type is quite simple - just set up a basic datapack (more information [here](https://minecraft.wiki/w/Data_pack)) and add some files under the `data/[namespace]/railways_liquid_fuel` directory. Files must end in `.json` but can be located in any sub-directory, as long as the full path does not violate Minecraft's ResourceLocation path check. The full specification for these JSON files can be found in the table below.

## JSON Specification

| Key          | Value Type   | Default Value | Description                                                                                                                                                                                  |
|--------------|--------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `fluids`     | string array | N/A           | Each string represents the namespaced identifier of an fluid or tag that holds fluids that will have this type applied to it.                                                                |
| `fuel_ticks` | integer      | `40`          | The amount of ticks of fuel time this the fluids above add to the timer, based on one tenth of a bucket, 100mb = 40 ticks by default (for context: 50 ticks is the default for lava buckets) |

## Other Information
The `fluids` array can contain either fluid id's `minecraft:water` or fluid tag id's `#c:water`.

Custom types will always override builtin types (types added through code),
but behavior is undefined if multiple custom types define the same fluid.
In the future, custom types defining the same fluid will override each other in alphabetical order.

## Examples

`data/example/railways_liquid_fuel/biofuel.json`
```json
{
  "fluids": [
    "createaddition:bioethanol",
    "garnished:peanut_oil"
  ],
  "fuel_ticks": 10
}
```

`data/example/railways_liquid_fuel/steam.json`
```json
{
  "fluids": [
    "mekanism:steam"
  ],
  "fuel_ticks": 20
}
```

`data/example/railways_liquid_fuel/water.json`
```json
{
  "fluids": [
    "#c:water"
  ],
  "fuel_ticks": 20
}
```