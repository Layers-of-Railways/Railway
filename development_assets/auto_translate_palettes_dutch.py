# Please do not run this unless you are Slimeist (techno-sam), the author of the script (or he has explained it to you)

import os
import json

prefix = "block.railways."

CAPITALIZE_FIRST_ONLY = False

colors: dict[str, str] = {
    "black": "zwarte",
    "blue": "blauwe",
    "brown": "bruine",
    "gray": "grijze",
    "green": "groene",
    "light_blue": "lichtblauwe",
    "light_gray": "lichtgrijze",
    "lime": "limoengroene",
    "magenta": "magenta",
    "orange": "oranje",
    "pink": "roze",
    "purple": "paarse",
    "red": "rode",
    "white": "witte",
    "yellow": "gele",
    "cyan": "turquoise",
    "": ""
}

color_keys = colors.keys()

wrapping_names: dict[str, str] = {
    "brass": "messing",
    "copper": "koper",
    "iron": "ijzer"
}

type_names = {
    "slashed": "gestreept lokometaal",
    "riveted": "geklonken lokometaal",
    "plated": "lokometaalplaat"
}


def capitalize(s: str) -> str:
    s = s.lower()
    if len(s) > 0:
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
        return join_with_title_case(colors[color], wrapping_names.get(wrapping, ""), ("bekleden", wrapping is not None), "lokometaalen stoommachine")
    return f


def mk_locometal(wrapping: str | None, flat: bool, typ: str | None) -> callable:
    """
    :param wrapping: None, brass, copper, iron
    :param flat: true/false
    :param typ: slashed, riveted, plated
    :return:
    """
    def f(color: str) -> str:
        return join_with_title_case(("platte", flat), colors[color], wrapping_names.get(wrapping, ""),
                                    ("bekleden", wrapping is not None), type_names.get(typ, "lokometaal"))
    return f


def mk_pillar() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors[color], "lokometaalpilaar")
    return f


def mk_smokebox() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors[color], "lokometaalen rookkast")
    return f


translations: dict[str, callable] = {
    "slashed_locometal": mk_locometal(None, False, "slashed"),
    "riveted_locometal": mk_locometal(None, False, "riveted"),
    "locometal_pillar": mk_pillar(),
    "locometal_smokebox": mk_smokebox(),
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
            print("Already translated", s)
            continue
        new_translated_strings[s] = formatter(color_name)

print(f"New translations for {lang}")
for k, v in new_translated_strings.items():
    print(f"  {k}: {v}")

# quit()
all_strings = existing_translated_strings.copy()
all_strings.update(new_translated_strings)
with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "w") as f:
    json.dump(all_strings, f, indent=2, ensure_ascii=False)
