# Please do not run this unless you are Slimeist (techno-sam), the author of the script (or he has explained it to you)

import json


CAPITALIZE_FIRST_ONLY = True
NON_CAPITALIZED_WORDS = {}

track_materials: dict[str, str] = {
    "acacia": "acacia",
    "birch": "bouleau",
    "dark_oak": "chêne noir",
    "jungle": "acajou",
    "oak": "chêne",
    "spruce": "sapin",
    "mangrove": "palétuvier",
    "warped": "biscornue",
    "crimson": "carmin",
    "blackstone": "roche noire",
    "ender": "ender",
    "tieless": "sans traverse",
    "phantom": "phantom",
    "cherry": "cerisier",
    "bamboo": "bambou",
    "stripped_bamboo": "bambou écorcé",
    "byg_aspen": "premble",
    "natures_spirit_aspen": "premble",
    "tfc_aspen": "premble",
    "byg_baobab": "baobab",

    "biomesoplenty_dead": "mort",
    "biomesoplenty_fir": "sapin",
    "biomesoplenty_hellbark": "écorce enfer",
    "biomesoplenty_jacaranda": "jacaranda",
    "biomesoplenty_magic": "magie",
    "biomesoplenty_mahogany": "acajou",
    "biomesoplenty_palm": "palmier",
    "biomesoplenty_redwood": "séquoia",
    "biomesoplenty_umbran": "umbran",
    "biomesoplenty_willow": "saule",
    "blue_skies_bluebright": "brillantbleu",
    "blue_skies_dusk": "crépuscule",
    "blue_skies_frostbright": "brilliantgel",
    "blue_skies_lunar": "lunaire",
    "blue_skies_maple": "érable",
    "blue_skies_starlit": "étoilé",
    "byg_blue_enchanted": "enchanté bleu",
    "byg_bulbis": "bulbis",
    "byg_cika": "cika",
    "byg_cypress": "scyprès",
    "byg_ebony": "ébène",
    "byg_embur": "embur",
    "byg_ether": "ether",
    "byg_green_enchanted": "enchanté vert",
    "byg_holly": "houx",
    "byg_imparius": "imparius",
    "byg_lament": "lament",
    "byg_nightshade": "belladone",
    "byg_pine": "pin",
    "byg_rainbow_eucalyptus": "arc-en-ciel de eucalyptus",
    "byg_skyris": "skyris",
    "byg_sythian": "sythian",
    "twilightforest_mangrove": "palétuvier",
    "tfc_mangrove": "palétuvier",
    "byg_witch_hazel": "noisetier",
    "byg_zelkova": "zelkova",
    "create_dd_rose": "rose",
    "create_dd_rubber": "caoutchouc",
    "create_dd_smoked": "fumé",
    "create_dd_spirit": "esprit",
    "hexcasting_edified": "edified",
    "natures_spirit_ghaf": "ghaf",
    "natures_spirit_joshua": "joshua",
    "natures_spirit_olive": "oliver",
    "natures_spirit_palo_verde": "palo verde",
    "natures_spirit_sugi": "sugi",
    "natures_spirit_wisteria": "glycine",
    "quark_azalea": "azalée",
    "tfc_ash": "ash",
    "tfc_blackwood": "bois noir",
    "tfc_chestnut": "marronnier",
    "tfc_hickory": "caryer",
    "tfc_kapok": "kapok",
    "tfc_sequoia": "sequoia",
    "tfc_sycamore": "sycamore",
    "tfc_white_cedar": "cèdre",
    "twilightforest_canopy": "canopy",
    "twilightforest_darkwood": "bois foncé",
    "twilightforest_minewood": "bois de mine",
    "twilightforest_sortingwood": "bois de tri",
    "twilightforest_timewood": "bois de temp",
    "twilightforest_transwood": "bois de transition",
    "twilightforest_twilight_oak": "crépuscule",
}

track_variants: dict[str, str] = {
    "wide": "large",
    "narrow": "étroite",
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


def mk_incomplete(variant: str | None) -> callable:
    """
    :param variant: None, wide, narrow
    :return:
    """
    def f(material: str) -> str:
        return join_with_title_case(
            "voie en",
            track_materials.get(material),
            track_variants.get(variant, ""),
            "incomplet",
        )
    return f


def mk_track(variant: str | None) -> callable:
    """
    :param variant: None, wide, narrow
    :return:
    """
    def f(material: str) -> str:
        return join_with_title_case(
            "voie en",
            track_materials.get(material),
            track_variants.get(variant, ""),
        )
    return f


examples = {
    # Incomplete
    "item.railways.track_incomplete_acacia": "Incomplete Acacia Track",
    "item.railways.track_incomplete_acacia_narrow": "Incomplete Narrow Acacia Track",
    "item.railways.track_incomplete_acacia_wide": "Incomplete Wide Acacia Track",

    # Blocks
    "block.railways.track_acacia": "Acacia Train Track",
    "block.railways.track_acacia_narrow": "Narrow Acacia Train Track",
    "block.railways.track_acacia_wide": "Wide Acacia Train Track",
}


translations: dict[str, callable] = {
    "item.railways.track_incomplete_{material}": mk_incomplete(None),
    "item.railways.track_incomplete_{material}_narrow": mk_incomplete("narrow"),
    "item.railways.track_incomplete_{material}_wide": mk_incomplete("wide"),

    "block.railways.track_{material}": mk_track(None),
    "block.railways.track_{material}_narrow": mk_track("narrow"),
    "block.railways.track_{material}_wide": mk_track("wide"),
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
    for mat in track_materials:
        s = string.format(material=mat)
        if s not in source_strings:
            print("OOPS", s)
            continue
        if s in existing_translated_strings:
            print("Already translated", s)
            continue
        new_translated_strings[s] = formatter(mat)

print(f"New translations for {lang}")
for k, v in new_translated_strings.items():
    print(f"  {k}: {v}")

# quit()
all_strings = existing_translated_strings.copy()
all_strings.update(new_translated_strings)
with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "w") as f:
    json.dump(all_strings, f, indent=2, ensure_ascii=False)
