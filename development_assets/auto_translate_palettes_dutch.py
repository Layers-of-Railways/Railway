# Please do not run this unless you are Slimeist (techno-sam), the author of the script (or he has explained it to you)

import os
import json

prefix = "block.railways."

CAPITALIZE_FIRST_ONLY = False
NON_CAPITALIZED_WORDS = {
    "met"
}

colors1: dict[str, str] = {
    "brown": "bruin",
    "maroon": "kastanjebruin",
    "red": "rood",
    "vermilion": "vermiljoen",
    "orange": "oranje",
    "granite": "graniet",
    "dripstone": "druipsteen",
    "ochrum": "oker",
    "yellow": "geel",
    "chartreuse": "chartreuse",
    "olive_green": "olijfgroen",
    "lime": "limoengroen",
    "green": "groen",
    "pine_green": "dennengroen",
    "cyan": "cyaan",
    "sea_green": "zeegroen",
    "turquoise": "turkoois",
    "light_blue": "lichtblauw",
    "blue": "blauw",
    "royal_blue": "koningsblauw",
    "purple": "paars",
    "magenta": "magenta",
    "pink": "roze",
    "white": "wit",
    "diorite": "dioriet",
    "limestone": "kalksteen",
    "light_gray": "lichtgrijs",
    "tuff": "tufsteen",
    "gray": "grijs",
    "scorchia": "scorchia",
    "black": "zwart",
    "": ""
}

colors2: dict[str, str] = {
    "brown": "bruine",
    "maroon": "kastanjebruine",
    "red": "rode",
    "vermilion": "vermiljoen",
    "orange": "oranje",
    "granite": "granieten",
    "dripstone": "druipstenen",
    "ochrum": "okeren",
    "yellow": "gele",
    "chartreuse": "chartreuse",
    "olive_green": "olijfgroene",
    "lime": "limoengroene",
    "green": "groene",
    "pine_green": "dennengroene",
    "cyan": "cyane",
    "sea_green": "zeegroene",
    "turquoise": "turkooizen",
    "light_blue": "lichtblauwe",
    "blue": "blauwe",
    "royal_blue": "koningsblauwe",
    "purple": "paarse",
    "magenta": "magenta",
    "pink": "roze",
    "white": "witte",
    "diorite": "diorieten",
    "limestone": "kalkstenen",
    "light_gray": "lichtgrijze",
    "tuff": "tufstenen",
    "gray": "grijze",
    "scorchia": "scorchia",
    "black": "zwarte",
    "": ""
}

color_keys = colors1.keys()

wrapping_names: dict[str, str] = {
    "brass": "messing",
    "copper": "koper",
    "iron": "ijzer"
}

type_names = {
    "slashed": "gesneden locometaal",
    "riveted": "geklonken locometaal",
    "plated": "locometalen platen"
}

type_colors = {
    "slashed": colors1,
    "riveted": colors1,
    "plated": colors2,
    None: colors1,
}


def capitalize(s: str) -> str:
    s = s.lower()
    if len(s) > 0 and s.lower() not in NON_CAPITALIZED_WORDS:
        if s.startswith("ij"):
            return "IJ" + s[2:]
        return s[0].upper() + s[1:]
    else:
        return s


def join_with_title_case(*parts: str | tuple[str, bool]) -> str:
    new_parts = []
    for p in parts:
        if type(p) == str:
            new_parts.append(p)
        elif type(p) == tuple:
            if p[1]:
                new_parts.append(p[0])
        else:
            raise ValueError(f"Invalid type {type(p)}")
    new_parts = [p for p in new_parts if p != ""]
    new_parts = " ".join(new_parts).split(" ")
    if CAPITALIZE_FIRST_ONLY:
        new_parts[0] = capitalize(new_parts[0])
    else:
        new_parts = [capitalize(p) for p in new_parts if p != ""]
    return " ".join(new_parts)


def mk_boiler(wrapping: str | None) -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors2[color],

                                    ("met", wrapping is not None),
                                    wrapping_names.get(wrapping, ""),
                                    ("beklede", wrapping is not None),

                                    "locometalen stoomketel")
    return f


def mk_locometal(wrapping: str | None, flat: bool, typ: str | None) -> callable:
    """
    :param wrapping: None, brass, copper, iron
    :param flat: true/false
    :param typ: slashed, riveted, plated
    :return:
    """
    def f(color: str) -> str:
        return join_with_title_case(("plat", flat),
                                    type_colors[typ][color],

                                    ("met", wrapping is not None),
                                    wrapping_names.get(wrapping, ""),
                                    ("beklede" if type_colors[typ] == colors2 else "bekleed", wrapping is not None),

                                    type_names.get(typ, "locometaal"))
    return f


def mk_pillar() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors2[color], "locometalen pilaar")
    return f

def mk_smokebox(wrapping: str | None) -> callable:
    """
    :param wrapping: None, copper, iron
    :return:
    """
    def f(color: str) -> str:
        return join_with_title_case(colors2[color],

                                    ("met", wrapping is not None),
                                    wrapping_names.get(wrapping, ""),
                                    ("beklede", wrapping is not None),

                                    "locometalen rookkast")
    return f

def mk_vent() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors2[color], "Locometalen Ventilatie")
    return f


def mk_flywheel() -> callable:
    def f(color: str):
        return join_with_title_case(colors1[color], "Locometalen Vliegwiel")
    return f

def mk_ladder(typ: str) -> callable:
    """
    :param typ: end, rung
    :return:
    """
    ladder_types = {
        "end": "Laddereind",
        "rung": "Ladder"
    }
    ladder_colors = {
        "end": colors1,
        "rung": colors2
    }
    def f(color: str) -> str:
        return join_with_title_case(ladder_colors[typ][color],
                                    "Locometalen",
                                    ladder_types[typ])
    return f

def mk_trapdoor() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors1[color], "Locometalen Valluik")
    return f

def mk_door(typ: str) -> callable:
    """
    :param typ: hinged, sliding, folding
    :return:
    """
    door_types = {
        "hinged": "deur",
        "sliding": "schuifdeur",
        "folding": "vouwdeur"
    }
    def f(color: str) -> str:
        return join_with_title_case(colors2[color],
                                    "Locometalen",
                                    door_types[typ])
    return f

def mk_window(typ: str) -> callable:
    """
    :param typ: round, single, two, four
    :return:
    """
    window_types = {
        "round": "rond",
        "single": "eenmalig",
        "two": "tweemalig",
        "four": "viermalig"
    }
    def f(color: str) -> str:
        return join_with_title_case(colors1[color],
                                    window_types[typ],
                                    "Geruit Locometalen Raam")
    return f

def mk_hazard(typ: str, on: str) -> callable:
    """
    :param typ: stripe, chevron
    :param on: black,white
    :return:
    """
    design_patterns = {
        "stripe": "gevarenstrepen",
        "chevron": "chevron"
    }
    def f(color: str) -> str:
        c = colors1[color] if color != "" else "Locometaal"
        return join_with_title_case(f'{c}-op-{colors2[on]}', design_patterns[typ])
    return f


translations: dict[str, callable] = {
    "slashed_locometal": mk_locometal(None, False, "slashed"),
    "riveted_locometal": mk_locometal(None, False, "riveted"),
    "locometal_pillar": mk_pillar(),

    "locometal_smokebox": mk_smokebox(None),
    "copper_wrapped_locometal_smokebox": mk_smokebox("copper"),
    "iron_wrapped_locometal_smokebox": mk_smokebox("iron"),

    "plated_locometal": mk_locometal(None, False, "plated"),
    "flat_slashed_locometal": mk_locometal(None, True, "slashed"),
    "flat_riveted_locometal": mk_locometal(None, True, "riveted"),

    "brass_wrapped_locometal": mk_locometal("brass", False, None),
    "iron_wrapped_locometal": mk_locometal("iron", False, None),
    "copper_wrapped_locometal": mk_locometal("copper", False, None),

    "locometal_boiler": mk_boiler(None),
    "brass_wrapped_locometal_boiler": mk_boiler("brass"),
    "copper_wrapped_locometal_boiler": mk_boiler("copper"),
    "iron_wrapped_locometal_boiler": mk_boiler("iron"),

    "locometal_vent": mk_vent(),
    "locometal_flywheel": mk_flywheel(),

    "locometal_end_ladder": mk_ladder("end"),
    "locometal_rung_ladder": mk_ladder("rung"),

    "locometal_trapdoor": mk_trapdoor(),
    "hinged_locometal_door": mk_door("hinged"),
    "sliding_locometal_door": mk_door("sliding"),
    "folding_locometal_door": mk_door("folding"),

    "round_pane_locometal_window": mk_window("round"),
    "single_pane_locometal_window": mk_window("single"),
    "two_pane_locometal_window": mk_window("two"),
    "four_pane_locometal_window": mk_window("four"),

    "hazard_stripes_chevron_on_black": mk_hazard("chevron", "black"),
    "hazard_stripes_chevron_on_white": mk_hazard("chevron", "white"),
    "hazard_stripes_diagonal_on_black": mk_hazard("stripe", "black"),
    "hazard_stripes_diagonal_on_white": mk_hazard("stripe", "white"),
}


with open("../common/src/generated/resources/assets/railways/lang/en_us.json", "r") as f:
    source_strings = json.load(f)
source_strings: dict[str, str]

lang = "nl_nl"

with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "r") as f:
    existing_translated_strings = json.load(f)
existing_translated_strings: dict[str, str]

new_translated_strings: dict[str, str] = {}

#for string in source_strings:
#    if sum(1 for exc in exclude if exc in string) != 0:
#        continue
#    if not string.startswith(prefix):
#        continue
#    if string not in existing_translated_strings:
#        continue
#
#    for suffix, format_string in conversions.items():
#        new_string = string + suffix
#        if new_string in existing_translated_strings:
#            continue
#        if new_string not in source_strings:
#            # print("OOPS", new_string)
#            continue
#        new_translated_strings[new_string] = format_string.format(existing_translated_strings[string])
for string, formatter in translations.items():
    string = "<COLOR>_" + string
    for color_name in color_keys:
        s = prefix + string.replace("<COLOR>", color_name).removeprefix("_").removesuffix("_")
        if s not in source_strings:
            print("OOPS", s)
            continue
        if s in existing_translated_strings:
            print("Already translated", s, "replacing")
            #continue
        new_translated_strings[s] = formatter(color_name)

print(f"New translations for {lang}")
for k, v in new_translated_strings.items():
    print(f"  {k}: {v}")

# quit()
all_strings = existing_translated_strings.copy()
all_strings.update(new_translated_strings)
with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "w") as f:
    json.dump(all_strings, f, indent=2, ensure_ascii=False)
