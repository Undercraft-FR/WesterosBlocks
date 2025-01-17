package com.westeroscraft.westerosblocks.blocks;

import java.util.List;
import java.util.Random;

import org.dynmap.modsupport.BlockTextureRecord;
import org.dynmap.modsupport.PatchBlockModel;
import org.dynmap.modsupport.ModModelDefinition;
import org.dynmap.modsupport.ModTextureDefinition;
import org.dynmap.modsupport.TextureModifier;
import org.dynmap.modsupport.TransparencyMode;
import org.dynmap.renderer.RenderPatchFactory.SideVisible;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.westeroscraft.westerosblocks.WesterosBlockDef;
import com.westeroscraft.westerosblocks.WesterosBlockDynmapSupport;
import com.westeroscraft.westerosblocks.WesterosBlockLifecycle;
import com.westeroscraft.westerosblocks.WesterosBlockFactory;
import com.westeroscraft.westerosblocks.WesterosBlockDef.Subblock;
import com.westeroscraft.westerosblocks.items.WCBedItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCBedBlock extends BlockBed implements WesterosBlockLifecycle, WesterosBlockDynmapSupport {

    public static class Factory extends WesterosBlockFactory {
        @Override
        public Block[] buildBlockClasses(WesterosBlockDef def) {
            def.setMetaMask(0x0);
            if (!def.validateMetaValues(new int[] { 0 }, new int[] { 0 })) {
                return null;
            }
            return new Block[] { new WCBedBlock(def) };
        }
    }
    
    private WesterosBlockDef def;
    
    protected WCBedBlock(WesterosBlockDef def) {
        super();
        this.def = def;
        def.doStandardContructorSettings(this);
    }

    public boolean initializeBlockDefinition() {
        def.doStandardInitializeActions(this);

        return true;
    }

    public boolean registerBlockDefinition() {
        WCBedItem.block = this;
        def.doStandardRegisterActions(this, WCBedItem.class);
        
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        def.doStandardRegisterIcons(iconRegister);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0)
        {
            return Blocks.planks.getBlockTextureFromSide(side);
        }
        else
        {
            int k = getDirection(meta);
            int l = Direction.bedDirection[k][side];
            int i1 = isBlockHeadOfBed(meta) ? 1 : 0;
            return (i1 != 1 || l != 2) && (i1 != 0 || l != 3) ? (l != 5 && l != 4 ? 
                    def.doStandardIconGet(1 - i1, 0) : def.doStandardIconGet(3 - i1, 0)) : def.doStandardIconGet(5 - i1, 0);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itm, CreativeTabs tab, List list) {
        def.getStandardSubBlocks(this, Item.getIdFromItem(itm), tab, list);
    }
    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(World par1World, int par2, int par3, int par4)
    {
        return Item.getItemFromBlock(this);
    }
    @Override
    public int damageDropped(int meta) {
        return 0;
    }
    @Override
    public WesterosBlockDef getWBDefinition() {
        return def;
    }
    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return def.getFireSpreadSpeed(world, x, y, z, face);
    }
    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return def.getFlammability(world, x, y, z, face);
    }
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return def.getLightValue(world, x, y, z);
    }
    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return def.getLightOpacity(this, world, x, y, z);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public int getBlockColor() {
        return def.getBlockColor();
    }
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderColor(int meta)
    {
        return def.getRenderColor(meta);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess access, int x, int y, int z)
    {
        return def.colorMultiplier(access, x, y, z);
    }
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return (def.alphaRender?1:0);
    }
    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return isBlockHeadOfBed(par1) ? null : Item.getItemFromBlock(this);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rnd) {
        def.doRandomDisplayTick(world, x, y, z, rnd);
        super.randomDisplayTick(world, x, y, z, rnd);
    }
    @Override
    public void registerDynmapRenderData(ModTextureDefinition mtd) {
        ModModelDefinition md = mtd.getModelDefinition();
        def.defaultRegisterTextures(mtd);
        WesterosBlockDef def = this.getWBDefinition();
        String blkname = def.getBlockName(0);
        Subblock sb = def.getByMeta(0);
        if ((sb != null) && (sb.textures != null)) {
            // Register textures 
            TextureModifier tmod = TextureModifier.NONE;
            if (def.nonOpaque) {
                tmod = TextureModifier.CLEARINSIDE;
            }
            BlockTextureRecord mtr = mtd.addBlockTextureRecord(blkname);
            mtr.setTransparencyMode(TransparencyMode.TRANSPARENT);
            // Set for all meta values for foot
            for (int meta = 0; meta < 8; meta++) {
                mtr.setMetaValue(meta);
            }
            int[] face_to_idx = new int[] { 1, 1, 3, 3, 5, 5 };
            for (int face = 0; face < 6; face++) {
                int fidx = face_to_idx[face];
                if (fidx >= sb.textures.size()) {
                    fidx = sb.textures.size() - 1;
                }
                String txtid = sb.textures.get(fidx);
                mtr.setPatchTexture(txtid.replace(':', '_'), tmod, face);
            }
            def.setBlockColorMap(mtr, sb);
            // Set for head
            mtr = mtd.addBlockTextureRecord(blkname);
            mtr.setTransparencyMode(TransparencyMode.TRANSPARENT);
            // Set for all meta values for head
            for (int meta = 8; meta < 16; meta++) {
                mtr.setMetaValue(meta);
            }
            face_to_idx = new int[] { 0, 0, 2, 2, 4, 4 };
            for (int face = 0; face < 6; face++) {
                int fidx = face_to_idx[face];
                if (fidx >= sb.textures.size()) {
                    fidx = sb.textures.size() - 1;
                }
                String txtid = sb.textures.get(fidx);
                mtr.setPatchTexture(txtid.replace(':', '_'), tmod, face);
            }
            def.setBlockColorMap(mtr, sb);
        }
        // Create east facing model
        PatchBlockModel mod = md.addPatchModel(blkname);
        mod.addPatch(0, 0.1875, 0, 1, 0.1875, 0, 0, 0.1875, 1, SideVisible.TOP); // Bottom
        mod.addPatch(0, 0.5625, 1, 1, 0.5625, 1, 0, 0.5625, 0, SideVisible.TOP); // Top
        mod.addPatch(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0.5625, 100, SideVisible.BOTTOM); // Z- (flip)
        mod.addPatch(0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0.5625, 100, SideVisible.TOP); // Z+
        mod.addPatch(0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0.5625, 100, SideVisible.TOP); // X-
        mod.addPatch(1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0.5625, 100, SideVisible.TOP); // X+
        mod.setMetaValue(3);
        mod.setMetaValue(7);
        mod.setMetaValue(11);
        mod.setMetaValue(15);
        // Make north facing model
        PatchBlockModel nmod = md.addRotatedPatchModel(blkname, mod, 0, 270, 0);
        nmod.setMetaValue(2);
        nmod.setMetaValue(6);
        nmod.setMetaValue(10);
        nmod.setMetaValue(14);
        // Make south facing model
        PatchBlockModel smod = md.addRotatedPatchModel(blkname, mod, 0, 90, 0);
        smod.setMetaValue(0);
        smod.setMetaValue(4);
        smod.setMetaValue(8);
        smod.setMetaValue(12);
        // Make west facing model
        PatchBlockModel wmod = md.addRotatedPatchModel(blkname, mod, 0, 180, 0);
        wmod.setMetaValue(1);
        wmod.setMetaValue(5);
        wmod.setMetaValue(9);
        wmod.setMetaValue(13);
    }

}
