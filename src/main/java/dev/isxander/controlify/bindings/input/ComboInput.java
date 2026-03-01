package dev.isxander.controlify.bindings.input;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.isxander.controlify.controller.input.ControllerStateView;
import dev.isxander.controlify.utils.CUtil;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record ComboInput(List<Input> inputs) implements Input {
    public static final String INPUT_ID = "combo";

    public static final MapCodec<ComboInput> CODEC = new MapCodec<>() {
        private final Supplier<MapCodec<ComboInput>> delegate = CUtil.lazyInit(
                () -> Input.CODEC.listOf().fieldOf("inputs").xmap(ComboInput::new, ComboInput::inputs)
        );

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return delegate.get().keys(ops);
        }

        @Override
        public <T> DataResult<ComboInput> decode(DynamicOps<T> ops, MapLike<T> input) {
            return delegate.get().decode(ops, input);
        }

        @Override
        public <T> RecordBuilder<T> encode(ComboInput input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return delegate.get().encode(input, ops, prefix);
        }
    };

    @Override
    public float state(ControllerStateView state) {
        if (inputs.isEmpty()) return 0;
        float min = Float.MAX_VALUE;
        for (Input input : inputs) {
            min = Math.min(min, input.state(state));
        }
        return min;
    }

    @Override
    public List<Identifier> getRelevantInputs() {
        return inputs.stream()
                .flatMap(input -> input.getRelevantInputs().stream())
                .toList();
    }

    @Override
    public InputType<?> type() {
        return InputType.COMBO;
    }
}
