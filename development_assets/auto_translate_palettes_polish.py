# Please do not run this unless you are Slimeist (techno-sam), the author of the script (or he has explained it to you)

import os
import json

prefix = "block.railways."

CAPITALIZE_FIRST_ONLY = True

color_masc: dict[str, str] = {
    "black": "czarny",
    "blue": "niebieski",
    "brown": "brązowy",
    "gray": "szary",
    "green": "zielony",
    "light_blue": "jasnoniebieski",
    "light_gray": "jasnoszary",
    "lime": "jasnozielony",
    "magenta": "karmazynowy",
    "orange": "pomarańczowy",
    "pink": "różowy",
    "purple": "fioletowy",
    "red": "czerwony",
    "white": "biały",
    "yellow": "żółty",
    "cyan": "błękit",
    "": ""
}

color_fem: dict[str, str] = {
    "black": "czarna",
    "blue": "niebieska",
    "brown": "brązowa",
    "gray": "szara",
    "green": "zielona",
    "light_blue": "jasnoniebieska",
    "light_gray": "jasnoszara",
    "lime": "jasnozielona",
    "magenta": "karmazynowa",
    "orange": "pomarańczowa",
    "pink": "różowa",
    "purple": "fioletowa",
    "red": "czerwona",
    "white": "biała",
    "yellow": "żółta",
    "cyan": "błękitna",
    "": ""
}

color_keys = color_masc.keys()

wrapping_names: dict[str, str] = {
    "brass": "mosiądzem",
    "copper": "miedzią",
    "iron": "żelazem"
}

type_names = {
    "slashed": "błyszczący",
    "riveted": "nitowany",
    "plated": "płytowy"
}


def capitalize(s: str) -> str:
    s = s.lower()
    if len(s) > 0 and s != "z":
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
        return join_with_title_case(color_masc[color], "kocioł z lokometalu",
                                    ("owinięty", wrapping is not None), wrapping_names.get(wrapping, ""))
    return f


def mk_locometal(wrapping: str | None, flat: bool, typ: str | None) -> callable:
    """
    :param wrapping: None, brass, copper, iron
    :param flat: true/false
    :param typ: slashed, riveted, plated
    :return:
    """
    def f(color: str) -> str:
        return join_with_title_case(("płaski", flat), color_masc[color], (type_names.get(typ, ""), typ is not None), "lokometal",
                                    ("owinięty", wrapping is not None), wrapping_names.get(wrapping, ""))
    return f


def mk_pillar() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(color_masc[color], "filar z lokometalu")
    return f


def mk_smokebox() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(color_fem[color], "dymnica z lokometalu")
    return f


translations: dict[str, callable] = {
    "slashed_locometal": mk_locometal(None, False, "slashed"),
    "riveted_locometal": mk_locometal(None, False, "riveted"),
    # todo pillar
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

lang = "pl_pl"

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
