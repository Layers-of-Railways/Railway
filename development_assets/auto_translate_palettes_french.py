#  Steam 'n' Rails
#  Copyright (c) 2025 The Railways Team
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Lesser General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
#  GNU Lesser General Public License for more details.
#
#  You should have received a copy of the GNU Lesser General Public License
#  along with this program. If not, see <https://www.gnu.org/licenses/>.

# Please do not run this unless you are Slimeist (techno-sam), the author of the script (or he has explained it to you)

import os
import json

prefix = "block.railways."

CAPITALIZE_FIRST_ONLY = True
NON_CAPITALIZED_WORDS = {
    "en",
    "de"
}

colors: dict[str, str] = {
    "black": "noir",
    "blue": "bleu",
    "brown": "marron",
    "gray": "gris",
    "green": "vert",
    "light_blue": "bleu claire",
    "light_gray": "gris claire",
    "lime": "vert citron",
    "magenta": "magenta",
    "orange": "orange",
    "pink": "rose",
    "purple": "violet",
    "red": "rouge",
    "white": "blanc",
    "yellow": "jaune",
    "": ""
}

color_keys = colors.keys()

wrapping_names: dict[str, str] = {
    "brass": "laiton",
    "copper": "cuivre",
    "iron": "fer"
}

type_names = {
    "slashed": "coupé",
    "riveted": "riveté",
    "plated": "plaqué"
}


def capitalize(s: str) -> str:
    s = s.lower()
    if len(s) > 0 and s.lower() not in NON_CAPITALIZED_WORDS:
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
        return join_with_title_case(
            "chaudière",
            colors[color],
            "en locométal",
            ("enveloppée de", wrapping is not None),
            wrapping_names.get(wrapping, ""),
        )
    return f


def mk_locometal(wrapping: str | None, flat: bool, typ: str | None) -> callable:
    """
    :param wrapping: None, brass, copper, iron
    :param flat: true/false
    :param typ: slashed, riveted, plated
    :return:
    """
    def f(color: str) -> str:
        return join_with_title_case(
            "locométal",
            ("plat", flat),
            type_names.get(typ, ""),
            ("enveloppée de", wrapping is not None),
            wrapping_names.get(wrapping, ""),
            colors[color],
        )
    return f


def mk_pillar() -> callable:
    def f(color: str) -> str:
        return join_with_title_case("pilier en locométal", colors[color])
    return f


def mk_smokebox() -> callable:
    def f(color: str) -> str:
        return join_with_title_case("boîte de fumée en locométal", colors[color])
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

lang = "fr_fr"

with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "r") as f:
    existing_translated_strings = json.load(f)
existing_translated_strings: dict[str, str]

new_translated_strings: dict[str, str] = {}

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
