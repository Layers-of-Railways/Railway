# Please do not run this unless you are Slimeist (techno-sam), the author of the script (or he has explained it to you)

import os
import json

prefix = "block.railways.track_"

exclude = [
    "incomplete",
    "wide",
    "narrow"
]

translations = {
    "nl_nl": {
        "_wide": "Breede {}",
        "_narrow": "Smalle {}"
    },
    "ja_jp": {
        "_wide": "広い{}",
        "_narrow": "狭い{}"
    }
}

with open("../common/src/generated/resources/assets/railways/lang/en_us.json", "r") as f:
    source_strings = json.load(f)
source_strings: dict[str, str]

for lang, conversions in translations.items():
    with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "r") as f:
        existing_translated_strings = json.load(f)
    existing_translated_strings: dict[str, str]

    new_translated_strings: dict[str, str] = {}

    for string in source_strings:
        if sum(1 for exc in exclude if exc in string) != 0:
            continue
        if not string.startswith(prefix):
            continue
        if string not in existing_translated_strings:
            continue

        for suffix, format_string in conversions.items():
            new_string = string + suffix
            if new_string in existing_translated_strings:
                continue
            if new_string not in source_strings:
                # print("OOPS", new_string)
                continue
            new_translated_strings[new_string] = format_string.format(existing_translated_strings[string])

    print(f"New translations for {lang}")
    for k, v in new_translated_strings.items():
        print(f"  {k}: {v}")

    all_strings = existing_translated_strings.copy()
    all_strings.update(new_translated_strings)
    with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "w") as f:
        json.dump(all_strings, f, indent="	", ensure_ascii=False)
