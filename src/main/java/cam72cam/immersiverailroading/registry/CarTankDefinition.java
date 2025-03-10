package cam72cam.immersiverailroading.registry;

import cam72cam.immersiverailroading.Config;
import cam72cam.immersiverailroading.ImmersiveRailroading;
import cam72cam.immersiverailroading.entity.CarTank;
import cam72cam.immersiverailroading.util.DataBlock;
import cam72cam.immersiverailroading.library.Gauge;
import cam72cam.immersiverailroading.library.GuiText;
import cam72cam.immersiverailroading.model.FreightTankModel;
import cam72cam.immersiverailroading.model.StockModel;
import cam72cam.immersiverailroading.util.FluidQuantity;
import cam72cam.mod.fluid.Fluid;
import cam72cam.mod.resource.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CarTankDefinition extends FreightDefinition {

    private List<Fluid> fluidFilter; // null == no filter
    private double capacity_l;

    public CarTankDefinition(String defID, DataBlock data) throws Exception {
        this(CarTank.class, defID, data);
    }

    CarTankDefinition(Class<? extends CarTank> type, String defID, DataBlock data) throws Exception {
        super(type, defID, data);
    }

    @Override
    protected Identifier defaultDataLocation() {
        return new Identifier(ImmersiveRailroading.MODID, "rolling_stock/default/tank.caml");
    }

    @Override
    public void loadData(DataBlock data) throws Exception {
        super.loadData(data);
        DataBlock tank = data.getBlock("tank");
        capacity_l = tank.getValue("capacity_l").asInteger() * internal_inv_scale;
        List<DataBlock.Value> whitelist = tank.getValues("whitelist");
        if (whitelist != null) {
            fluidFilter = new ArrayList<>();
            for (DataBlock.Value allowed : whitelist) {
                Fluid allowedFluid = Fluid.getFluid(allowed.asString());
                if (allowedFluid == null) {
                    ImmersiveRailroading.warn("Skipping unknown whitelisted fluid: " + allowed);
                    continue;
                }
                fluidFilter.add(allowedFluid);
            }
        }
    }

    @Override
    public List<String> getTooltip(Gauge gauge) {
        List<String> tips = super.getTooltip(gauge);
        tips.add(GuiText.TANK_CAPACITY_TOOLTIP.toString(this.getTankCapaity(gauge).Buckets()));
        return tips;
    }

    public FluidQuantity getTankCapaity(Gauge gauge) {
        FluidQuantity cap = FluidQuantity.FromLiters((int) Math.ceil(this.capacity_l * gauge.scale())).min(FluidQuantity.FromBuckets(1));
        return Config.ConfigBalance.RoundStockTankToNearestBucket ? cap.roundBuckets() : cap;
    }

    public List<Fluid> getFluidFilter() {
        return this.fluidFilter;
    }

    @Override
    protected StockModel<?, ?> createModel() throws Exception {
        return new FreightTankModel<>(this);
    }
}
