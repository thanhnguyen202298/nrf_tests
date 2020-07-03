package no.nordicsemi.android.mesh.transport;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.utils.SecureUtils;

/**
 * To be used as a wrapper class when creating a GenericOnOffSet message.
 */
@SuppressWarnings("unused")
public class GenericOnOffSet extends GenericMessage {

    private static final String TAG = GenericOnOffSet.class.getSimpleName();
    private static int OP_CODE = ApplicationMessageOpCodes.GENERIC_ON_OFF_STATUS;
    private static final int GENERIC_ON_OFF_SET_TRANSITION_PARAMS_LENGTH = 4;
    private static final int GENERIC_ON_OFF_SET_PARAMS_LENGTH = 2;
    private static final int GENERIC_ON_OFF_STATE_PARAMS_LENGTH = 4;

    private final byte Cmd;
    private int address = -1;
    private final int tId;
    private int countNode =-1;
    private byte[] colors;

    public static final byte LOCAL_ASSIGN = (byte) 0xA0;
    public static final byte LOCAL_START = (byte) 0x10;
    public static final byte LOCAL_STOP = (byte) 0x11;
    public static final byte LOCAL_UPDATE = (byte) 0xA3;
    public static final byte LOCAL_CHECK = (byte) 0xA4;
    public static final byte LOCAL_BASE = (byte) 0xA5;
    public static final byte LOCAL_CHANGE = (byte) 0xB0;
    public static final byte LOCAL_DEFAULT = (byte) 0xFF;

    /**
     * Constructs GenericOnOffSet message.
     *
     * @param appKey  {@link ApplicationKey} key for this message
     * @param address Boolean state of the GenericOnOffModel
     * @param tId     Transaction id
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public GenericOnOffSet(@NonNull final ApplicationKey appKey,
                           final int address,
                           final int tId) throws IllegalArgumentException {
        this(appKey, address, tId, 0, (byte) 0, OP_CODE);
    }

    /**
     * Constructs GenericOnOffSet message.
     *
     * @param appKey  {@link ApplicationKey} key for this message
     * @param address Boolean state of the GenericOnOffModel
     * @param tId     Transaction id
     * @param cmd     Delay for this message to be executed 0 - 1275 milliseconds
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public GenericOnOffSet(@NonNull final ApplicationKey appKey,
                           final int address,
                           final int tId,
                           final int countNode,
                           @Nullable final byte cmd, int typeOpeCode) {
        super(appKey);
        this.Cmd = cmd;
        this.address = address;
        this.tId = tId;
        this.countNode = countNode;
        this.OP_CODE = typeOpeCode;
        assembleMessageParameters();
    }

    public GenericOnOffSet(@NonNull final ApplicationKey appKey,
                           final int tId,
                           @Nullable final byte cmd, int typeOpeCode, byte[] colors) {
        super(appKey);
        this.Cmd = cmd;
        this.tId = tId;
        this.OP_CODE = typeOpeCode;
        this.colors = colors;
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey.getKey());
        final ByteBuffer paramsBuffer;
        Log.v(TAG, "State: " + (address > -1 ? "ON" : "OFF"));
        paramsBuffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        if (Cmd == LOCAL_ASSIGN) {
            paramsBuffer.put((byte) tId);
            paramsBuffer.put(this.Cmd);
            paramsBuffer.put((byte) address);
            paramsBuffer.put((byte) countNode);
        } else {
            if (colors != null) {
                paramsBuffer.put((byte) tId);
                paramsBuffer.put(this.Cmd);
                paramsBuffer.put(colors[0]);
                paramsBuffer.put(colors[1]);
                paramsBuffer.put(colors[2]);
            }else {
                paramsBuffer.put((byte) tId);
                paramsBuffer.put(this.Cmd);
            }
        }
        mParameters = paramsBuffer.array();

    }
}
