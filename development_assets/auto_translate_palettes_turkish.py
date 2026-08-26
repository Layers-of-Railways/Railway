# Please do not run this unless you are Slimeist (techno-sam), the author of the script (or he has explained it to you)

import os
import json

prefix = "block.railways."

CAPITALIZE_FIRST_ONLY = False
NON_CAPITALIZED_WORDS = {
    "ve"
}

colors: dict[str, str] = {
    "black": "siyah",
    "blue": "mavi",
    "brown": "kahverengi",
    "chartreuse": "şartröz",
    "cyan": "camgöbeği",
    "diorite": "diyorit",
    "dripstone": "damla taşı",
    "granite": "granit",
    "gray": "gri",
    "green": "yeşil",
    "light_blue": "açık mavi",
    "light_gray": "açık gri",
    "lime": "açık yeşil",
    "limestone": "kireç",
    "magenta": "eflatun",
    "maroon": "bordo",
    #"netherite": "netherit",
    "ochrum": "okrum",
    "olive_green": "zeytin yeşili",
    "orange": "turuncu",
    "pine_green": "çam yeşili",
    "pink": "pembe",
    "purple": "mor",
    "red": "kırmızı",
    "royal_blue": "koyu mavi",
    "scorchia": "korza",
    "sea_green": "deniz yeşili",
    "tuff": "tüf",
    "turquoise": "turkuaz",
    "vermilion": "parlak kırmızı",
    "white": "beyaz",
    "yellow": "sarı",
    "": ""
}

color_keys = colors.keys()

wrapping_names: dict[str, str] = {
    "brass": "pirinç",
    "copper": "bakır",
    "iron": "demir"
}

type_names = {
    "slashed": "kesik",
    "riveted": "perçinlenmiş",
    "plated": "kaplanmış"
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
        return join_with_title_case(colors[color], wrapping_names.get(wrapping, ""), ("Sarılı", wrapping is not None), "Lokometal Kazan")
    return f


def mk_locometal(wrapping: str | None, flat: bool, typ: str | None) -> callable:
    """
    :param wrapping: None, brass, copper, iron
    :param flat: true/false
    :param typ: slashed, riveted, plated
    :return:
    """
    def f(color: str) -> str:
        return join_with_title_case(("Düz ve", flat),
                                    wrapping_names.get(wrapping, ""), ("Sarılı", wrapping is not None),
                                    (type_names.get(typ, ""), typ is not None),
                                    colors[color],
                                    "lokometal")
    return f


def mk_pillar() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors[color], "lokometal sütunu")
    return f


def mk_smokebox(wrapping: str | None) -> callable:
    """
    :param wrapping: None, copper, iron
    :return:
    """
    def f(color: str) -> str:
        return join_with_title_case(colors[color],
                                    wrapping_names.get(wrapping, ""), ("Sarılı", wrapping is not None),
                                    "Lokometal Baca")
    return f

def mk_vent() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors[color], "Lokometal Havalandırma Bloğu")
    return f


def mk_flywheel() -> callable:
    def f(color: str):
        return join_with_title_case(colors[color], "Lokometal Volan")
    return f

def mk_ladder(typ: str) -> callable:
    """
    :param typ: end, rung
    :return:
    """
    ladder_types = {
        "end": "Kancalı Merdiven",
        "rung": "Menhol Basamakları"
    }
    def f(color: str) -> str:
        return join_with_title_case(colors[color], "Lokometal", ladder_types[typ])
    return f

def mk_trapdoor() -> callable:
    def f(color: str) -> str:
        return join_with_title_case(colors[color], "Lokometal Tuzak Kapısı")
    return f

def mk_door(typ: str) -> callable:
    """
    :param typ: hinged, sliding, folding
    :return:
    """
    door_types = {
        "hinged": "Menteşeli",
        "sliding": "Sürgülü",
        "folding": "Katlanır"
    }
    def f(color: str) -> str:
        return join_with_title_case(colors[color], door_types[typ], "Lokometal Kapı")
    return f

def mk_window(typ: str) -> callable:
    """
    :param typ: round, single, two, four
    :return:
    """
    window_types = {
        "round": "Yuvarlak Camlı",
        "single": "Tek Bölmeli",
        "two": "Çift Bölmeli",
        "four": "Dört Bölmeli"
    }
    def f(color: str) -> str:
        return join_with_title_case(colors[color], window_types[typ], "Lokometal Pencere")
    return f

def mk_hazard(typ: str, on: str) -> callable:
    """
    :param typ: stripe, chevron
    :param on: black,white
    :return:
    """
    design_patterns = {
        "stripe": "Uyarı Şeritli Blok",
        "chevron": "Şevron Desenli Blok"
    }
    def f(color: str) -> str:
        return join_with_title_case(colors[on], "Üzerine", colors[color], design_patterns[typ])
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

lang = "tr_tr"

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
