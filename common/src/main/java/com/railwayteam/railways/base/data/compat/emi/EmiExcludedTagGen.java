package com.railwayteam.railways.base.data.compat.emi;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.railwayteam.railways.multiloader.CommonTag;
import com.railwayteam.railways.multiloader.CommonTags;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;

public class EmiExcludedTagGen implements DataProvider {
    private static final String INDENT = "  ";
    private final PackOutput packOutput;

    public EmiExcludedTagGen(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public void run(CachedOutput output) throws IOException {
        Path path = this.packOutput.getOutputFolder()
            .resolve("assets/emi/tag/exclusions/railways.json");

        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

        Writer writer = new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8);
        writer.append(run());
        writer.close();

        output.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
    }

    private String run() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n")
            .append(INDENT)
            .append("\"item\": [\n");
        // fill in items
        Iterator<CommonTag<Item>> itemIterator = CommonTags.ALL_ITEMS.iterator();
        while (itemIterator.hasNext()) {
            CommonTag<Item> itemTag = itemIterator.next();
            builder.append(INDENT).append(INDENT).append("\"");
            builder.append(itemTag.tag.location());
            builder.append("\"");
            if (itemIterator.hasNext()) {
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append(INDENT)
            .append("],\n")
            .append(INDENT)
            .append("\"block\": [\n");
        // fill in blocks
        Iterator<CommonTag<Block>> blockIterator = CommonTags.ALL_BLOCKS.iterator();
        while (blockIterator.hasNext()) {
            CommonTag<Block> blockTag = blockIterator.next();
            builder.append(INDENT).append(INDENT).append("\"");
            builder.append(blockTag.tag.location());
            builder.append("\"");
            if (blockIterator.hasNext()) {
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append(INDENT)
                .append("]\n");
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String getName() {
        return "Steam 'n' Rails EMI excluded tags";
    }
}
