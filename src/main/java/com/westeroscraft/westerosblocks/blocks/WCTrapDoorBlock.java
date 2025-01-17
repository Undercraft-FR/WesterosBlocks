package com.westeroscraft.westerosblocks.blocks;

import java.util.List;
import java.util.Random;

import org.dynmap.modsupport.ModTextureDefinition;
import org.dynmap.modsupport.TransparencyMode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.westeroscraft.westerosblocks.WesterosBlockDef;
import com.westeroscraft.westerosblocks.WesterosBlockDynmapSupport;
import com.westeroscraft.westerosblocks.WesterosBlockLifecycle;
import com.westeroscraft.westerosblocks.WesterosBlockFactory;
import com.westeroscraft.westerosblocks.WesterosBlockDef.BoundingBox;
import com.westeroscraft.westerosblocks.items.MultiBlockItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCTrapDoorBlock extends BlockTrapDoor implements WesterosBlockLifecycle, WesterosBlockDynmapSupport {

    public static class Factory extends WesterosBlockFactory {
        @Override
        public Block[] buildBlockClasses(WesterosBlockDef def) {
            def.setMetaMask(0x0);
            if (!def.validateMetaValues(new int[] { 0 }, new int[] { 0 })) {
                return null;
            }
            return new Block[] { new WCTrapDoorBlock(def) };
        }
    }
    
    private WesterosBlockDef def;
    
    protected WCTrapDoorBlock(WesterosBlockDef def) {
        super(def.getMaterial());
        this.def = def;
        def.doStandardContructorSettings(this);
    }

    public boolean initializeBlockDefinition() {
        def.doStandardInitializeActions(this);

        return true;
    }

    public boolean registerBlockDefinition() {
        def.doStandardRegisterActions(this, MultiBlockItem.class);
        
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
        return def.doStandardIconGet(side, meta);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itm, CreativeTabs tab, List list) {
        def.getStandardSubBlocks(this, Item.getIdFromItem(itm), tab, list);
    }
    @Override
    public int damageDropped(int meta) {
        return meta;
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
    public void registerDynmapRenderData(ModTextureDefinition mtd) {
        def.defaultRegisterTextures(mtd);
        def.defaultRegisterTextureBlock(mtd, 0, (def.alphaRender ? TransparencyMode.TRANSPARENT : TransparencyMode.OPAQUE));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rnd) {
        def.doRandomDisplayTick(world, x, y, z, rnd);
        super.randomDisplayTick(world, x, y, z, rnd);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity)
    {
        if (def.hasCollisionBoxes() == false) {
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            return;
        }
        int meta = world.getBlockMetadata(x,  y,  z);
        List<BoundingBox> cl = def.getCollisionBoxList(meta); 
        if (cl == null) {
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            return;
        }
        for (BoundingBox c : cl) {
            this.setBlockBounds(c.xMin, c.yMin, c.zMin, c.xMax, c.yMax, c.zMax);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
        def.setBlockBoundsBasedOnState(this, world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        if (def.hasCollisionBoxes() == false) {
            return super.collisionRayTrace(world, x, y, z, start, end);
        }
        int meta = world.getBlockMetadata(x,  y,  z);
        List<BoundingBox> cl = def.getCollisionBoxList(meta); 
        if (cl == null) {
            return super.collisionRayTrace(world, x, y, z, start, end);
        }
        MovingObjectPosition bestpos = null;
        double bestdist = 0.0;
        for (BoundingBox c : cl) {
            this.setBlockBounds(c.xMin, c.yMin, c.zMin, c.xMax, c.yMax, c.zMax);
            MovingObjectPosition pos = super.collisionRayTrace(world, x, y, z, start, end);
            if (pos != null) {
                if (bestpos == null) {
                    bestpos = pos;
                    bestdist = bestpos.hitVec.squareDistanceTo(end);
                }
                else {
                    double dist = pos.hitVec.squareDistanceTo(end);
                    if (dist > bestdist) {
                        bestpos = pos;
                        bestdist = dist;
                    }
                }
            }
        }
        def.setBlockBoundsBasedOnState(this, world, x, y, z);
        return bestpos;
    }
}
