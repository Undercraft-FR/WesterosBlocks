package com.westeroscraft.westerosblocks.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.westeroscraft.westerosblocks.WesterosBlockDef;
import com.westeroscraft.westerosblocks.WesterosBlockLifecycle;
import com.westeroscraft.westerosblocks.WesterosBlockFactory;
import com.westeroscraft.westerosblocks.WesterosBlocks;
import com.westeroscraft.westerosblocks.items.WCCuboidNEStackItem;

public class WCCuboidNEStackBlock extends WCCuboidNEBlock implements WesterosBlockLifecycle {

    public static class Factory extends WesterosBlockFactory {
        @Override
        public Block[] buildBlockClasses(WesterosBlockDef def) {
            def.setMetaMask(0x7);
            if (!def.validateMetaValues(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }, new int[] { 0, 1 })) {
                return null;
            }
            // Force any top blocks to have no inventory item
            if (def.subBlocks != null) {
                int matches = 0;
                for (WesterosBlockDef.Subblock sb : def.subBlocks) {
                    if ((sb.meta & 0x1) == 1) {
                        sb.noInventoryItem = true;
                    }
                    matches |= (1 << sb.meta);
                }
                for (int i = 0; i < 4; i++) {
                    switch (matches >> (2*i)) {
                        case 0:
                        case 3:
                            break;
                        default:
                            WesterosBlocks.log.severe(String.format("unmatched stacked subblocks %d in block '%s'", 2*i, def.blockType));
                            return null;
                    }
                }
            }
            return new Block[] { new WCCuboidNEStackBlock(def) };
        }
    }
    
    private boolean noBreakUnder[] = new boolean[4];
    
    protected WCCuboidNEStackBlock(WesterosBlockDef def) {
        super(def);
        if (def.subBlocks != null) {
            for (WesterosBlockDef.Subblock sb : def.subBlocks) {
                if ((sb.meta & 1) == 0) {
                    String type = def.getType(sb.meta);
                    if (type == null) continue;
                    String[] toks = type.split(",");
                    for (String tok : toks) {
                        if (tok.equals("no-break-under")) {
                            noBreakUnder[(sb.meta >> 1) & 0x3] = true;
                        }
                    }
                }
            }
        }
    }

    public boolean registerBlockDefinition() {
        def.doStandardRegisterActions(this, WCCuboidNEStackItem.class);
        
        return true;
    }
   
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
    {
        int meta = world.getBlockMetadata(x, y, z);

        // If we're a bottom block
        if ((meta & 1) == 0) {
            boolean didBreak = false;
            Block above = world.getBlock(x, y + 1, z);
            int aboveMeta = 0;
            boolean aboveIsTop = false;
            if (above == this) {
                aboveMeta = world.getBlockMetadata(x, y + 1, z);
                if (aboveMeta == (meta | 0x1)) {
                    aboveIsTop = true;
                }
            }
            // Check if block above is still our top
            if (!aboveIsTop) {
                // If not, break us
                world.setBlockToAir(x, y, z);
                didBreak = true;
            }
            // Did we lose our support block and not a 'no-break-under' block?
            if ((!noBreakUnder[(meta >> 1) & 0x3]) && (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z))) {
                world.setBlockToAir(x, y, z);
                didBreak = true;
                // See if above is still our top - break it too, if needed
                if (aboveIsTop)
                {
                    world.setBlockToAir(x, y + 1, z);
                }
            }
            // If we broke the block, drop item
            if (didBreak) {
                if (!world.isRemote) {
                    this.dropBlockAsItem(world, x, y, z, meta, 0);
                }
            }
        }
        else {  // Else its a top block
            Block below = world.getBlock(x, y - 1, z);
            int belowMeta = 0;
            boolean belowIsBottom = false;
            if (below == this) {
                belowMeta = world.getBlockMetadata(x, y - 1, z);
                if (belowMeta == (meta & 0xE)) {
                    belowIsBottom = true;
                }
            }
            // If below isn't our bottom
            if (!belowIsBottom) {
                // Break us too
                world.setBlockToAir(x, y, z);
            }
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
    public Item getItemDropped(int meta, Random rnd, int other)
    {
        // If top, no drop
        if ((meta & 1) == 1) {
            return null;
        }
        else {
            return super.getItemDropped(meta, rnd, other);
        }
    }
    @Override
    public int damageDropped(int meta) {
        if ((meta & 1) == 1) {  // Top
            return 0;
        }
        return super.damageDropped(meta);
    }
    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return y >= 255 ? false : super.canPlaceBlockAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y + 1, z);
    }
}
